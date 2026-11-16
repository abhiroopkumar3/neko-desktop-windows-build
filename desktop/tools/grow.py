"""Grow shared-sources.txt: add candidate files that declare names the compiler reports as unresolved.

Complements prune.py (which only shrinks). Run: python3 -I tools/grow.py CANDIDATES.txt [rounds]
"""
import re, os, subprocess, sys, collections

R = os.path.abspath("..") + "/"
CAND = [l.strip() for l in open(sys.argv[1]) if l.strip()]
ROUNDS = int(sys.argv[2]) if len(sys.argv) > 2 else 6
decl = re.compile(r"^(?:\s*(?:public|internal|data|sealed|enum|abstract|open|annotation|inline|value|suspend|const|expect|actual|fun|private)\s+)*(?:class|object|interface|typealias|fun|val|var)\s+(?:<[^>]+>\s*)?(?:[\w.<>?, ]+\.)?(\w+)", re.M)
byname = collections.defaultdict(set)
for e in CAND:
    mod, rel = e.split("/", 1)
    base = {"app": "app/src/main/java/", "core": "core/src/main/kotlin/", "constants": "constants/src/main/kotlin/"}.get(mod)
    for b in ([base] if base else []):
        p = R + b + rel
        if os.path.exists(p):
            for d in decl.finditer(open(p, errors="ignore").read()):
                byname[d.group(1)].add(e)

for rnd in range(ROUNDS):
    cur = {l.strip() for l in open("shared-sources.txt") if l.strip()}
    out = subprocess.run(["./gradlew", "--no-daemon", "-q", "compileKotlin", "-x", "kspKotlin"], capture_output=True, text=True)
    text = out.stdout + out.stderr
    names = set(re.findall(r"Unresolved reference '(\w+)'", text)) | set(re.findall(r"Unresolved reference: (\w+)", text))
    add = set()
    for n in names:
        for e in byname.get(n, ()):
            if e not in cur: add.add(e)
    print(f"round {rnd}: {len(cur)} files, {len(names)} unresolved names, adding {len(add)}", flush=True)
    if out.returncode == 0 or not add: break
    open("shared-sources.txt", "w").write("\n".join(sorted(cur | add)) + "\n")
