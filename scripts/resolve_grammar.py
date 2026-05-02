#!/usr/bin/env python3
"""Resolve `$apply` macros in vscode-xonsh's grammar YAML and emit JSON.

vscode-xonsh ships `dist/tmlang-xonsh.json` produced by `js-yaml`, which leaves
the custom `$apply` macro unexpanded. VS Code's TextMate engine tolerates the
resulting unresolved `#includes`; IntelliJ's does not. Run this once after every
upstream grammar update (`vscode-xonsh` checked out as a sibling directory).
"""
import json
import re
import sys
from pathlib import Path

import yaml

HERE = Path(__file__).resolve().parent
SRC = (HERE / "../../vscode-xonsh/src/tmlang").resolve()
OUT = (HERE / "../src/main/resources/textmate/xonsh/syntaxes/xonsh.tmLanguage.json").resolve()


def substitute(text: str, vars: dict) -> str:
    # Replace ${name} with the value. Use a function form to avoid backref
    # interpretation in re.sub.
    def repl(m):
        name = m.group(1)
        return vars.get(name, m.group(0))
    return re.sub(r"\$\{(\w+)\}", repl, text)


def load_apply(entry: dict) -> dict:
    path = SRC / entry["file"]
    text = path.read_text()
    vars = entry.get("vars", {})
    # Default empty string for any unspecified ${name}, otherwise YAML may break.
    text = re.sub(r"\$\{(\w+)\}", lambda m: vars.get(m.group(1), ""), text)
    return yaml.safe_load(text)


def resolve(node):
    if isinstance(node, dict):
        if "$apply" in node:
            applied = {}
            for entry in node["$apply"]:
                fragment = load_apply(entry)
                if not isinstance(fragment, dict):
                    raise SystemExit(f"applied fragment not a dict: {entry}")
                # detect collisions
                for k in fragment:
                    if k in applied:
                        print(f"warn: duplicate repository key {k!r} from {entry['file']} {entry.get('vars',{})}", file=sys.stderr)
                applied.update(fragment)
            # merge other keys of node (besides $apply)
            merged = {k: v for k, v in node.items() if k != "$apply"}
            for k in applied:
                if k in merged:
                    print(f"warn: applied overrides existing key {k!r}", file=sys.stderr)
            merged.update(applied)
            return {k: resolve(v) for k, v in merged.items()}
        return {k: resolve(v) for k, v in node.items()}
    if isinstance(node, list):
        return [resolve(v) for v in node]
    return node


def main():
    src = SRC / "xonsh.syntax.yaml"
    grammar = yaml.safe_load(src.read_text())
    grammar = resolve(grammar)
    OUT.write_text(json.dumps(grammar, indent=2) + "\n")
    print(f"wrote {OUT} ({OUT.stat().st_size} bytes)")


if __name__ == "__main__":
    main()