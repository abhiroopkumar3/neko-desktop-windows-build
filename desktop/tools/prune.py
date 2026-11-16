"""Shrink shared-sources.txt to the largest set of upstream files that compiles on desktop.

Runs the desktop compile, drops every file the compiler reports an error in, and repeats until
the build is green. Files that only fail because a dependency was dropped are removed in later
rounds. Run from the desktop/ directory:  python3 -I tools/prune.py
"""
import re, subprocess, sys, os

LIST = "shared-sources.txt"
ERR = re.compile(r"^e: (?:\[ksp\] )?(?:file://)?.*?/build/shared-src/(.+?\.kt):\d+(?::\d+)?")
MAX_ROUNDS = int(sys.argv[1]) if len(sys.argv) > 1 else 15

def entries():
    return [l.strip() for l in open(LIST) if l.strip() and not l.startswith("#")]

def resolve(rel, current):
    # shared-src merges the three module roots, so match by suffix
    return [e for e in current if e.split("/", 1)[1] == rel]

for rnd in range(1, MAX_ROUNDS + 1):
    current = entries()
    out = subprocess.run(["./gradlew", "--no-daemon", "-q", "compileKotlin", "-x", "kspKotlin"], capture_output=True, text=True)
    text = out.stdout + out.stderr
    bad_files = {m.group(1) for line in text.splitlines() if (m := ERR.match(line))}
    other = [l for l in text.splitlines() if l.startswith("e: ") and not ERR.match(l)]
    print(f"round {rnd}: {len(current)} files, {len(bad_files)} failing files, {len(other)} other errors", flush=True)
    if out.returncode == 0:
        print("BUILD GREEN with", len(current), "files"); break
    if not bad_files:
        print("failed without attributable file errors:\n" + "\n".join(text.splitlines()[-40:])); sys.exit(1)
    drop = {e for rel in bad_files for e in resolve(rel, current)}
    protect = {l.strip() for l in open("tools/protect.txt")} if os.path.exists("tools/protect.txt") else set()
    hit = drop & protect
    if hit:
        print("protected files fail, fix these first:")
        for line in text.splitlines():
            m = ERR.match(line)
            if m and any(h.split("/", 1)[1] == m.group(1) for h in hit): print("  " + line.split("/build/shared-src/")[-1][:200])
        sys.exit(2)
    if not drop:
        print("could not map failing files to list entries:", sorted(bad_files)[:5]); sys.exit(1)
    open(LIST, "w").write("\n".join(sorted(set(current) - drop)) + "\n")
else:
    print("stopped after", MAX_ROUNDS, "rounds; not yet green")
