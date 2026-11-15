import os, re, sys, collections
root = sys.argv[1]
ALL = '--all' in sys.argv
sys.argv = [a for a in sys.argv if a != '--all']
mods = {"app": "app/src/main/java", "core": "core/src/main/kotlin", "constants": "constants/src/main/kotlin"}
# imports that cannot compile on desktop JVM without a shim or replacement
bad_prefix = ("android.", "androidx.room", "androidx.work", "androidx.core.", "androidx.appcompat",
              "androidx.biometric", "androidx.preference", "androidx.fragment", "androidx.viewpager",
              "androidx.recyclerview", "androidx.constraintlayout", "androidx.palette", "androidx.webkit",
              "androidx.browser", "androidx.activity", "androidx.vectordrawable", "androidx.viewbinding",
              "androidx.lifecycle.process", "com.google.firebase", "com.google.android", "eu.kanade.tachiyomi.R",
              "org.nekomanga.R", "me.saket.cascade", "tachiyomi.decoder", "com.mikepenz",
              "com.hippo.unifile", "org.conscrypt")
imp = re.compile(r'^import\s+([\w.]+)', re.M)
res = collections.defaultdict(lambda: collections.Counter())
total = collections.Counter(); free = collections.Counter(); freeloc = collections.Counter(); loc = collections.Counter()
reasons = collections.Counter()
emit = []
for m, rel in mods.items():
    base = os.path.join(root, rel)
    for dp, _, fs in os.walk(base):
        for f in fs:
            if not f.endswith(".kt"): continue
            p = os.path.join(dp, f); s = open(p, encoding="utf-8", errors="ignore").read()
            n = s.count("\n"); total[m] += 1; loc[m] += n
            hits = [i for i in imp.findall(s) if i.startswith(bad_prefix)]
            # also uses of R. / BuildConfig / Context without import are caught by compile later
            if not hits or ALL:
                free[m] += 1; freeloc[m] += n; emit.append(m + "/" + os.path.relpath(p, base))
            else:
                for h in set(hits):
                    for bp in bad_prefix:
                        if h.startswith(bp): reasons[bp] += 1; break
for m in mods: print(f"{m}: {free[m]}/{total[m]} files import-clean ({freeloc[m]}/{loc[m]} LOC)")
if len(sys.argv) > 2:
    open(sys.argv[2], "w").write("\n".join(sorted(emit)) + "\n")
print("\nTop blockers (files affected):")
for k, v in reasons.most_common(18): print(f"  {k:28s} {v}")
