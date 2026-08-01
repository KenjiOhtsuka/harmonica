#!/usr/bin/env bash
set -euo pipefail

# Remove the MIT license header comment block from source files.
# Handles both the /* */ (kt/kts/java) and # (properties/services) forms.
# Skips generated docs/api, .idea, and .git.
# Idempotent: files already stripped are left untouched.

root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"

targets="$(mktemp)"
trap 'rm -f "$targets"' EXIT

grep -rl "released under the MIT License" . \
  --exclude-dir=.git --exclude-dir=docs --exclude-dir=.idea \
  | sort > "$targets"

count=0
while IFS= read -r file; do
  [ -z "$file" ] && continue
  first="$(sed -n '1p' "$file")"
  case "$first" in
    '/*'|'#') ;;
    *) continue ;;
  esac
  # Block is exactly 7 lines followed by a blank line.
  if [ "$(sed -n '5p' "$file")" = " * This software is released under the MIT License." ] \
     || [ "$(sed -n '5p' "$file")" = "# This software is released under the MIT License." ]; then
    if [ -z "$(sed -n '8p' "$file")" ]; then
      sed -i '1,8d' "$file"
    else
      sed -i '1,7d' "$file"
    fi
    count=$((count + 1))
  fi
done < "$targets"

echo "Stripped headers from $count file(s)."
