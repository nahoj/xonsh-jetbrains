#!/usr/bin/env python3
"""Compile TextMate grammar JSON from vscode-xonsh's source YAML.

This implements file-inclusion logic similar to that of the `syntaxdev`
tool. I don't know if it is exactly the same, but it seems good enough
to build our grammar file.
"""
import json
import re
import sys
from pathlib import Path

import yaml

HERE = Path(__file__).resolve().parent
SRC = (HERE / "../../vscode-xonsh/src/tmlang").resolve()
OUT = (HERE / "../src/main/resources/textmate/xonsh/syntaxes/xonsh.tmLanguage.json").resolve()


def load_apply(entry: dict) -> dict:
    path = SRC / entry["file"]
    text = path.read_text()
    variables = entry.get("vars", {})
    def _subst(m):
        name = m.group(1)
        if name not in variables:
            print(f"warn: undefined var ${{{name}}} in {entry['file']}", file=sys.stderr)
        # If undefined, leave ${name} as-is like syntaxdev does
        return variables.get(name, m.group(0))
    text = re.sub(r"\$\{(\w+)}", _subst, text)
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