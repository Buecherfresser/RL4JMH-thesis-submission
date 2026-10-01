# ruff: noqa: E501 - this module is one embedded HTML/JS document, not Python source
"""The single-page UI, inlined.

No CDN and no build step: the page is served from the training box and viewed through an SSH
tunnel, so every byte has to be local. Charts are hand-rolled SVG -- a line chart with an optional
smoothed overlay is all this needs, and it keeps the dependency list at zero.
"""

from __future__ import annotations

INDEX_HTML = r"""<!doctype html>
<html lang="en"><head><meta charset="utf-8"><title>GRPO telemetry</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
:root{
  --bg:#0f1115; --panel:#171a21; --line:#242938; --fg:#e6e9ef; --dim:#9aa3b2;
  --accent:#4da3ff; --ok:#3fb950; --warn:#d29922; --bad:#f85149; --grid:#222735;
}
@media (prefers-color-scheme: light){
  :root{ --bg:#f6f7f9; --panel:#fff; --line:#e3e6ec; --fg:#12151b; --dim:#5b6472;
         --grid:#eceef3; }
}
*{box-sizing:border-box}
body{margin:0;background:var(--bg);color:var(--fg);
  font:14px/1.5 ui-sans-serif,-apple-system,"Segoe UI",Roboto,sans-serif}
header{position:sticky;top:0;z-index:10;background:var(--panel);border-bottom:1px solid var(--line);
  padding:10px 16px;display:flex;gap:12px;align-items:center;flex-wrap:wrap}
h1{font-size:15px;margin:0;font-weight:650;letter-spacing:.2px}
select,button,a.btn{background:var(--bg);color:var(--fg);border:1px solid var(--line);
  border-radius:6px;padding:5px 10px;font:inherit;cursor:pointer;text-decoration:none}
button:hover,a.btn:hover{border-color:var(--accent)}
.spacer{flex:1}
.dim{color:var(--dim)}
main{padding:16px;max-width:1500px;margin:0 auto}
.tabs{display:flex;gap:6px;margin-bottom:14px;flex-wrap:wrap}
.tab{padding:6px 12px;border-radius:6px;border:1px solid transparent;cursor:pointer}
.tab.active{background:var(--panel);border-color:var(--line)}
.cards{display:grid;grid-template-columns:repeat(auto-fill,minmax(180px,1fr));gap:10px;margin-bottom:16px}
.card{background:var(--panel);border:1px solid var(--line);border-radius:8px;padding:10px 12px}
.card .k{font-size:11px;text-transform:uppercase;letter-spacing:.6px;color:var(--dim)}
.card .v{font-size:20px;font-weight:650;margin-top:2px;font-variant-numeric:tabular-nums}
.card .d{font-size:12px;color:var(--dim)}
.grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(430px,1fr));gap:12px}
.panel{background:var(--panel);border:1px solid var(--line);border-radius:8px;padding:10px 12px}
.panel h3{margin:0 0 2px;font-size:13px;font-weight:600}
.panel .sub{font-size:11.5px;color:var(--dim);margin-bottom:6px}
table{border-collapse:collapse;width:100%;font-size:12.5px}
th,td{text-align:left;padding:5px 8px;border-bottom:1px solid var(--line);
  font-variant-numeric:tabular-nums}
th{color:var(--dim);font-weight:600;position:sticky;top:0;background:var(--panel)}
td.num,th.num{text-align:right}
.scroll{max-height:460px;overflow:auto}
.pill{display:inline-block;padding:1px 7px;border-radius:999px;font-size:11px;font-weight:600}
.pill.ok{background:rgba(63,185,80,.15);color:var(--ok)}
.pill.warn{background:rgba(210,153,34,.15);color:var(--warn)}
.pill.bad{background:rgba(248,81,73,.15);color:var(--bad)}
pre{background:var(--bg);border:1px solid var(--line);border-radius:6px;padding:10px;
  overflow:auto;max-height:520px;font:12px/1.45 ui-monospace,SFMono-Regular,Menlo,monospace}
.rollouts{display:grid;grid-template-columns:repeat(auto-fill,minmax(400px,1fr));gap:10px}
.mono{font-family:ui-monospace,SFMono-Regular,Menlo,monospace;font-size:12px}
.empty{padding:26px;text-align:center;color:var(--dim)}
label{font-size:12px;color:var(--dim)}
</style></head><body>
<header>
  <h1>GRPO telemetry</h1>
  <select id="run"></select>
  <label><input type="checkbox" id="live" checked> live</label>
  <span id="stamp" class="dim"></span>
  <span class="spacer"></span>
  <a class="btn" id="dl-md">markdown</a>
  <select id="csv-table">
    <option value="steps">steps</option>
    <option value="steps_per_rank">steps (per rank)</option>
    <option value="components">reward components</option>
    <option value="rollouts">rollouts</option>
    <option value="projects">projects</option>
    <option value="failures">failures</option>
    <option value="gpu">gpu</option>
  </select>
  <a class="btn" id="dl-csv">download csv</a>
</header>
<main>
  <div class="tabs" id="tabs"></div>
  <div id="view"></div>
</main>
<script>
const $ = (s,r=document)=>r.querySelector(s);
const fmt = (v,d=3)=> (v===null||v===undefined||Number.isNaN(v)) ? "—" : Number(v).toFixed(d);
const pct = (v,d=1)=> (v===null||v===undefined) ? "—" : (100*v).toFixed(d)+" %";
const esc = s => String(s??"").replace(/[&<>"]/g,c=>({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;"}[c]));
let STATE={run:null,tab:"quality",data:null,comp:null};

/* ---- charting: one small SVG line chart, optional smoothed overlay ---- */
function chart(series, {height=150, pct:asPct=false, zero=false}={}){
  const raw=(series&&series.raw)||[], sm=(series&&series.smooth)||[];
  if(!raw.length) return '<div class="empty">no data</div>';
  const W=520,H=height,P={t:8,r:10,b:20,l:42};
  const xs=raw.map(p=>p[0]), all=raw.concat(sm).map(p=>p[1]);
  let lo=Math.min(...all), hi=Math.max(...all);
  if(zero) lo=Math.min(0,lo);
  if(hi===lo){hi=lo+1;}
  const pad=(hi-lo)*0.08; lo-=pad; hi+=pad;
  const x0=Math.min(...xs), x1=Math.max(...xs)||1;
  const sx=v=>P.l+(W-P.l-P.r)*((v-x0)/((x1-x0)||1));
  const sy=v=>P.t+(H-P.t-P.b)*(1-((v-lo)/((hi-lo)||1)));
  const path=pts=>pts.map((p,i)=>(i?"L":"M")+sx(p[0]).toFixed(1)+" "+sy(p[1]).toFixed(1)).join("");
  let ticks="";
  for(let i=0;i<=3;i++){
    const v=lo+(hi-lo)*i/3, y=sy(v);
    ticks+=`<line x1="${P.l}" x2="${W-P.r}" y1="${y}" y2="${y}" stroke="var(--grid)"/>`+
           `<text x="${P.l-6}" y="${y+3.5}" text-anchor="end" font-size="10" fill="var(--dim)">`+
           `${asPct?(100*v).toFixed(0)+"%":(Math.abs(v)>=100?v.toFixed(0):v.toPrecision(2))}</text>`;
  }
  return `<svg viewBox="0 0 ${W} ${H}" width="100%" height="${H}" preserveAspectRatio="none">
    ${ticks}
    <path d="${path(raw)}" fill="none" stroke="var(--accent)" stroke-opacity="${sm.length?0.28:0.9}" stroke-width="1.2"/>
    ${sm.length?`<path d="${path(sm)}" fill="none" stroke="var(--accent)" stroke-width="2"/>`:""}
    <text x="${P.l}" y="${H-6}" font-size="10" fill="var(--dim)">step ${x0|0}</text>
    <text x="${W-P.r}" y="${H-6}" font-size="10" fill="var(--dim)" text-anchor="end">${x1|0}</text>
  </svg>`;
}
const panel=(title,sub,body)=>`<div class="panel"><h3>${title}</h3><div class="sub">${sub}</div>${body}</div>`;
const cchart=(t,s,key,o)=>panel(t,s,chart(STATE.data.charts[key],o));

/* ---- tabs ---- */
const TABS=[["quality","Quality"],["composition","Reward composition"],
            ["performance","Performance"],["failures","Failures"],
            ["projects","Projects"],["generations","Generations"]];

function renderTabs(){
  $("#tabs").innerHTML=TABS.map(([k,l])=>
    `<div class="tab ${STATE.tab===k?"active":""}" data-tab="${k}">${l}</div>`).join("");
  document.querySelectorAll(".tab").forEach(el=>el.onclick=()=>{
    STATE.tab=el.dataset.tab; render();
    if(STATE.tab==="generations") loadCompletions();
  });
}

function healthPills(s){
  const rows=[];
  const p=(cond,label,val,why)=>rows.push(
    `<tr><td>${label}</td><td class="num">${val}</td><td><span class="pill ${cond}">${
      cond==="ok"?"ok":cond==="warn"?"watch":"fail"}</span> <span class="dim">${why||""}</span></td></tr>`);
  const roc=s.run_over_compile;
  p(roc===null||roc>=0.85?"ok":"warn","ran / compiled",fmt(roc,2),
    roc!==null&&roc<0.85?"compiled benchmarks are not being measured":"");
  if(!s.jmh_lock_recorded) p("warn","rollouts lost to the JMH lock","not recorded",
    "this run predates run_diagnostics — a lost lock looks identical to a benchmark that threw");
  else p(s.jmh_lock_losses?"bad":"ok","rollouts lost to the JMH lock",s.jmh_lock_losses,
    s.jmh_lock_losses?"false-negative zeros — set a per-invocation java.io.tmpdir":"");
  const z=s.zero_std_frac_last;
  p(z===null||z<0.5?"ok":"warn","zero-advantage steps (last 50)",pct(z),
    z!==null&&z>=0.5?"over half of recent steps produced no gradient":"");
  const el=s.mutation_eligible_frac;
  p(el===null||el>=0.15?"ok":"warn","rollouts reaching the mutation term",pct(el),
    el!==null&&el<0.15?"the 0.70-weight signal is rarely exercised":"");
  const ms=s.mutation_score_mean;
  p(ms===null||ms>=0.5?"ok":"warn","mutation score (eligible)",fmt(ms,3),
    ms!==null&&ms<0.5?"is concurrent JMH perturbing the slowdown test?":"");
  return `<table><thead><tr><th>check</th><th class="num">value</th><th>verdict</th></tr></thead>
    <tbody>${rows.join("")}</tbody></table>
    <div class="sub" style="margin-top:8px">Weight sync is certified in the run log
    (<span class="mono">[weight-sync] VERIFIED</span>), not here — runs 1–6 sampled every rollout
    from the base model and no metric in this directory could see it.</div>`;
}

function cards(s){
  const d=(a,b)=> (a===null||b===null)?"":`${(100*(b-a)>=0?"+":"")}${(100*(b-a)).toFixed(1)} pp since start`;
  return `<div class="cards">
    <div class="card"><div class="k">step</div><div class="v">${s.last_step??"—"}</div>
      <div class="d">epoch ${fmt(s.epoch,2)}</div></div>
    <div class="card"><div class="k">reward</div><div class="v">${fmt(s.reward_last)}</div>
      <div class="d">from ${fmt(s.reward_first)}</div></div>
    <div class="card"><div class="k">compile</div><div class="v">${pct(s.compile_last)}</div>
      <div class="d">${d(s.compile_first,s.compile_last)}</div></div>
    <div class="card"><div class="k">ran / compiled</div><div class="v">${fmt(s.run_over_compile,2)}</div>
      <div class="d">1.0 = all measured</div></div>
    <div class="card"><div class="k">mutation score</div><div class="v">${fmt(s.mutation_score_mean,3)}</div>
      <div class="d">${pct(s.mutation_eligible_frac)} eligible</div></div>
    <div class="card"><div class="k">step time</div><div class="v">${fmt(s.step_time_last,1)} s</div>
      <div class="d">${fmt(s.wall_clock_h,1)} h total</div></div>
    <div class="card"><div class="k">rollouts</div><div class="v">${s.rollouts}</div>
      <div class="d">${s.steps} steps</div></div>
    <div class="card"><div class="k">zero-advantage</div><div class="v">${pct(s.zero_std_frac_last,0)}</div>
      <div class="d">of the last 50 steps</div></div>
  </div>`;
}

function viewQuality(){
  const c=STATE.data.charts;
  return cards(STATE.data.summary)
   + panel("Health","Thresholds from measured run-7/run-8 behaviour",healthPills(STATE.data.summary))
   + '<div class="grid" style="margin-top:12px">'
   + cchart("Reward","mean over the group; thin line raw, thick smoothed","reward",{zero:true})
   + cchart("Reward spread (std)","zero spread = zero advantage = no gradient from that step","reward_std",{zero:true})
   + cchart("Zero-advantage steps","fraction of steps whose whole group scored identically","zero_std_frac",{pct:true,zero:true})
   + cchart("Compile rate","share of rollouts that compiled","compile_frac",{pct:true,zero:true})
   + cchart("ran / compiled","below ~0.9 means compiled benchmarks are not being scored","run_over_compile",{zero:true})
   + cchart("Parse failures","completions the parser could not extract Java from","parse_fail_frac",{pct:true,zero:true})
   + cchart("Mutation-eligible","rollouts that reached the 0.70-weight term at all","mutation_eligible_frac",{pct:true,zero:true})
   + cchart("Mutation score","mean over eligible rollouts","mutation_score_mean",{zero:true})
   + cchart("Mutation coverage rate","share of mutants the benchmark actually covered","mutation_coverage_rate_mean",{zero:true})
   + '</div>';
}

function viewComposition(){
  const rw=STATE.data.realised_weights, nominal={compile:0.10,runtime:0.10,anti_pattern:0.05,rsd:0.05,mutation:0.70};
  const rows=rw.map(r=>`<tr><td>${r.component}</td><td class="num">${pct(r.realised_share)}</td>
    <td class="num">${fmt(nominal[r.component],2)}</td>
    <td class="num">${r.realised_share!==null?fmt(r.realised_share/(nominal[r.component]||1),2)+"×":"—"}</td></tr>`).join("");
  let charts="";
  for(const n of ["compile","runtime","anti_pattern","rsd","mutation"]){
    charts+=cchart(n+" (raw)","component score before weighting",n+"_mean",{zero:true});
  }
  for(const n of ["compile","runtime","anti_pattern","rsd","mutation"]){
    charts+=cchart(n+" (weighted)","contribution to the reward the policy actually sees","weighted_"+n+"_mean",{zero:true});
  }
  return panel("Realised vs nominal weights",
      "The mutation term only pays on rollouts that compiled <em>and</em> ran, so its realised share "
    + "falls far below its nominal weight while compile's rises above. This is why a run can improve "
    + "compile rate while losing benchmark coverage.",
      `<table><thead><tr><th>component</th><th class="num">realised share</th>
       <th class="num">nominal weight</th><th class="num">ratio</th></tr></thead><tbody>${rows}</tbody></table>`)
   + '<div class="grid" style="margin-top:12px">'+charts+'</div>';
}

function viewPerformance(){
  const s=STATE.data.summary, c=STATE.data.charts;
  let gpu="";
  for(const [k,t,o] of [["gpu_util","GPU utilisation",{pct:true,zero:true}],
                        ["gpu_mem_used_frac","GPU memory used",{pct:true,zero:true}],
                        ["gpu_power_w","GPU power (W)",{zero:true}],
                        ["gpu_temp_c","GPU temperature (°C)",{}]]){
    if(c[k]) gpu+=cchart(t,"sampled every few seconds; x-axis is wall-clock",k,o);
  }
  if(!gpu) gpu=panel("GPU","not sampled for this run",
    '<div class="empty">Set <span class="mono">gpu_sample_seconds</span> in the GRPO config to record '
   +'<span class="mono">metrics/gpu.csv</span>. Idle cards during a step are the CPU-side JMH reward.</div>');
  return `<div class="cards">
      <div class="card"><div class="k">mean step</div><div class="v">${fmt(s.step_time_mean,1)} s</div></div>
      <div class="card"><div class="k">recent step</div><div class="v">${fmt(s.step_time_last,1)} s</div>
        <div class="d">rises as compile rate rises</div></div>
      <div class="card"><div class="k">total</div><div class="v">${fmt(s.wall_clock_h,2)} h</div></div>
    </div>`
   + '<div class="grid">'
   + cchart("Step time","wall clock per optimiser step","step_time",{zero:true})
   + cchart("Completion length","mean generated tokens per rollout","completion_length",{zero:true})
   + cchart("Entropy","policy entropy; a collapse or a runaway are both worth catching","entropy",{})
   + cchart("Gradient norm","update magnitude","grad_norm",{zero:true})
   + cchart("Learning rate","scheduler output — check it matches the run it is compared against","learning_rate",{zero:true})
   + cchart("Loss","dr_grpo token-mean loss","loss",{})
   + cchart("Terminator-id mismatch (TRL clipped_ratio)",
       "NOT truncation. TRL flags &quot;unterminated&quot; by testing one EOS id, but Gemma 4 declares "
     + "three (<span class=\"mono\">[1, 106, 50]</span>) and uses two: ~57% of completions end on "
     + "<span class=\"mono\">1 &lt;eos&gt;</span> and are miscounted here. Harmless while "
     + "<span class=\"mono\">mask_truncated_completions: false</span>; if that were true it would "
     + "zero most of every batch. Use the next panel for real truncation.",
       "clipped_ratio",{pct:true,zero:true})
   + cchart("Longest completion in the step",
       "the real truncation signal, in tokens — compare against max_completion_length (16384). "
     + "Run 8: mean 2253, p90 2644, and only 1.1% of step-rows reach the cap at all.",
       "max_completion_length",{zero:true})
   + gpu + '</div>';
}

function viewFailures(){
  const f=STATE.data.tables.failures;
  const ct=f.compile.map(r=>`<tr><td class="num">${r.count}</td><td class="num">${pct(r.share_of_failures)}</td>
    <td class="mono">${esc(r.message)}</td></tr>`).join("")||'<tr><td colspan="3" class="dim">none</td></tr>';
  const rt=f.run.map(r=>`<tr><td class="num">${r.count}</td><td class="mono">${esc(r.message)}</td></tr>`).join("")
    ||'<tr><td colspan="2" class="dim">none</td></tr>';
  return panel("Compile failures","javac diagnostics, deduplicated per rollout",
      `<div class="scroll"><table><thead><tr><th class="num">n</th><th class="num">of failures</th>
       <th>diagnostic</th></tr></thead><tbody>${ct}</tbody></table></div>`)
   + panel("Run failures",
      "Why a compiled benchmark produced no measurement. A <span class=\"mono\">jmh.lock</span> "
     +"line here means rollouts are being zeroed by lock contention, not by their own bugs.",
      `<div class="scroll"><table><thead><tr><th class="num">n</th><th>diagnostic</th></tr></thead>
       <tbody>${rt}</tbody></table></div>`);
}

function viewProjects(){
  const rows=STATE.data.tables.projects.map(r=>`<tr><td>${esc(r.project)}</td>
    <td class="num">${r.n}</td><td class="num">${pct(r.compile_frac)}</td>
    <td class="num">${pct(r.run_frac)}</td><td class="num">${fmt(r.reward_mean,3)}</td>
    <td class="num">${fmt(r.seconds_per_compiled,1)}</td></tr>`).join("");
  return panel("Per-project yield",
    "Sorted worst-first by compile rate. A project near 0 % costs a full rollout batch of wall "
   +"clock and contributes no advantage spread — but dropping every hard subject quietly turns "
   +"the benchmark into an easy-subject benchmark.",
    `<div class="scroll"><table><thead><tr><th>project</th><th class="num">rollouts</th>
     <th class="num">compile</th><th class="num">ran</th><th class="num">reward</th>
     <th class="num">s / compiled</th></tr></thead><tbody>${rows}</tbody></table></div>`);
}

/* ---- generations ---- */
async function loadCompletions(){
  STATE.comp=await (await fetch(`/api/completions?run=${encodeURIComponent(STATE.run)}`)).json();
  render();
}
function viewGenerations(){
  const c=STATE.comp;
  if(!c) return '<div class="empty">loading…</div>';
  if(!c.captured) return panel("Generations","nothing captured for this run",
    '<div class="empty">Set <span class="mono">capture_completions_every: 25</span> in the GRPO '
   +'config. It writes a bounded sample of rollout source to '
   +'<span class="mono">metrics/completions.jsonl</span> — roughly 2 MB per 1500-step run — and is '
   +'the only way to compare a subject\'s benchmark across training, or the G siblings within one step.</div>');
  const steps=c.steps.map(s=>`<option value="${s.step}">step ${s.step} (${s.rollouts})</option>`).join("");
  const snips=c.across_steps.map(s=>
    `<option value="${esc(s.snippet_id)}">${esc(s.snippet_id.split(".").pop())} — ${s.steps.length} steps</option>`).join("");
  return panel("Compare generations",
    "<b>Within a step</b>: pick a step to see the G rollouts of the same prompt side by side — same "
   +"input, different samples, different rewards. <b>Across training</b>: pick a subject to see how "
   +"its benchmark changed as the policy learned.",
    `<div style="display:flex;gap:10px;flex-wrap:wrap;align-items:center">
       <label>step <select id="g-step"><option value="">any</option>${steps}</select></label>
       <label>subject <select id="g-snip"><option value="">any</option>${snips}</select></label>
       <button id="g-go">show</button>
       <span class="dim">${c.rows} captured rollouts</span>
     </div>`)
   + '<div id="g-out"></div>';
}
async function showGenerations(){
  const step=$("#g-step").value, snip=$("#g-snip").value;
  const q=`run=${encodeURIComponent(STATE.run)}&step=${encodeURIComponent(step)}&snippet=${encodeURIComponent(snip)}`;
  const d=await (await fetch(`/api/completion?${q}`)).json();
  if(!d.rows.length){ $("#g-out").innerHTML='<div class="empty">no rollouts match</div>'; return; }
  $("#g-out").innerHTML='<div class="rollouts">'+d.rows.map(r=>{
    const ok=r.compiled?(r.ran?"ok":"warn"):"bad";
    const tag=r.compiled?(r.ran?"compiled + ran":"compiled, no run"):"compile error";
    const diag=[...(r.compile_diagnostics||[]),...(r.run_diagnostics||[])].slice(0,3)
      .map(x=>`<div class="dim mono">${esc(x)}</div>`).join("");
    return `<div class="panel"><h3>step ${r.step} · rank ${esc(r.rank)} · #${r.index}
        <span class="pill ${ok}" style="float:right">${tag}</span></h3>
      <div class="sub">${esc(r.snippet_id||"")}<br>reward ${fmt(r.reward,3)}
        · ${fmt(r.duration_s,1)} s${r.mutation&&r.mutation.score!==undefined&&r.mutation.score!==null
        ?" · mutation "+fmt(r.mutation.score,3):""}</div>
      ${diag}<pre>${esc(r.source)}</pre></div>`;
  }).join("")+'</div>';
}

/* ---- shell ---- */
function render(){
  renderTabs();
  if(!STATE.data){ $("#view").innerHTML='<div class="empty">loading…</div>'; return; }
  const v={quality:viewQuality,composition:viewComposition,performance:viewPerformance,
           failures:viewFailures,projects:viewProjects,generations:viewGenerations}[STATE.tab];
  $("#view").innerHTML=v();
  if(STATE.tab==="generations" && $("#g-go")) $("#g-go").onclick=showGenerations;
  const r=encodeURIComponent(STATE.run);
  $("#dl-md").href=`/export/markdown?run=${r}`;
  $("#dl-csv").href=`/export/csv?run=${r}&table=${$("#csv-table").value}`;
}
async function refresh(){
  if(!STATE.run) return;
  try{
    STATE.data=await (await fetch(`/api/overview?run=${encodeURIComponent(STATE.run)}`)).json();
    $("#stamp").textContent="updated "+new Date().toLocaleTimeString();
    // The generations tab holds user state -- a chosen step/subject and the rendered sources.
    // Re-rendering it on the 10s poll would silently discard that mid-read, and its content
    // comes from a different endpoint that the poll does not even refresh.
    if(STATE.tab!=="generations") render();
  }catch(e){ $("#stamp").textContent="refresh failed"; }
}
async function boot(){
  const runs=await (await fetch("/api/runs")).json();
  $("#run").innerHTML=runs.runs.map(r=>`<option>${esc(r)}</option>`).join("");
  STATE.run=runs.runs[runs.runs.length-1]||null;
  if(STATE.run) $("#run").value=STATE.run;
  $("#run").onchange=()=>{STATE.run=$("#run").value;STATE.comp=null;refresh();};
  $("#csv-table").onchange=render;
  await refresh();
  setInterval(()=>{ if($("#live").checked) refresh(); }, 10000);
}
boot();
</script></body></html>
"""
