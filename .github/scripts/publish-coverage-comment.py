#!/usr/bin/env python3
"""Parse JaCoCo XML reports and publish a module + overall coverage table on the PR."""

import json
import os
import sys
import urllib.error
import urllib.request
import xml.etree.ElementTree as ET
from typing import Dict, List, Optional, Tuple, Union

MARKER = "<!-- jacoco-module-coverage-report -->"
MODULES = [
    ("user-module", "user-module/target/site/jacoco/jacoco.xml"),
    ("account-module", "account-module/target/site/jacoco/jacoco.xml"),
]


def parse_instruction_coverage(xml_path: str) -> Tuple[float, int, int]:
    root = ET.parse(xml_path).getroot()
    for counter in root.findall("counter"):
        if counter.get("type") == "INSTRUCTION":
            missed = int(counter.get("missed", 0))
            covered = int(counter.get("covered", 0))
            total = missed + covered
            pct = (covered / total * 100.0) if total else 0.0
            return pct, covered, total
    return 0.0, 0, 0


def api_request(
    method: str, url: str, token: str, payload: Optional[Dict] = None
) -> Union[dict, list]:
    data = None
    headers = {
        "Authorization": f"Bearer {token}",
        "Accept": "application/vnd.github+json",
        "X-GitHub-Api-Version": "2022-11-28",
    }
    if payload is not None:
        data = json.dumps(payload).encode("utf-8")
        headers["Content-Type"] = "application/json"

    request = urllib.request.Request(url, data=data, headers=headers, method=method)
    with urllib.request.urlopen(request) as response:
        body = response.read().decode("utf-8")
        return json.loads(body) if body else {}


def find_existing_comment(repo: str, pr_number: int, token: str) -> Optional[int]:
    url = f"https://api.github.com/repos/{repo}/issues/{pr_number}/comments?per_page=100"
    comments = api_request("GET", url, token)
    for comment in comments:
        if MARKER in comment.get("body", ""):
            return comment["id"]
    return None


def build_body(
    rows: List[tuple[str, float, str]],
    overall_pct: float,
    overall_status: str,
    min_coverage: float,
    sha: str,
    run_url: str,
) -> str:
    lines = [
        MARKER,
        "## Code Coverage Report",
        "",
        f"Commit: `{sha[:7]}` | [View workflow run]({run_url})",
        "",
        "| Module | Instruction Coverage | Minimum (50%) | Status |",
        "| --- | ---: | ---: | :---: |",
    ]

    for module, pct, status in rows:
        lines.append(f"| `{module}` | {pct:.2f}% | {min_coverage:.0f}% | {status} |")

    lines.extend(
        [
            f"| **Overall** | **{overall_pct:.2f}%** | **{min_coverage:.0f}%** | **{overall_status}** |",
            "",
            "_Updated automatically on every pull request commit._",
        ]
    )
    return "\n".join(lines)


def main() -> int:
    token = os.environ.get("GITHUB_TOKEN")
    repo = os.environ.get("GITHUB_REPOSITORY")
    pr_number = os.environ.get("PR_NUMBER")
    sha = os.environ.get("GITHUB_SHA", "unknown")
    run_id = os.environ.get("GITHUB_RUN_ID", "")
    server_url = os.environ.get("GITHUB_SERVER_URL", "https://github.com")
    min_coverage = float(os.environ.get("MIN_COVERAGE", "50"))

    if not token or not repo or not pr_number:
        print("Missing GITHUB_TOKEN, GITHUB_REPOSITORY, or PR_NUMBER", file=sys.stderr)
        return 1

    run_url = f"{server_url}/{repo}/actions/runs/{run_id}" if run_id else f"{server_url}/{repo}/actions"
    pr_number_int = int(pr_number)

    total_covered = 0
    total_all = 0
    rows: List[Tuple[str, float, str]] = []

    for module, xml_path in MODULES:
        if not os.path.isfile(xml_path):
            print(f"Missing JaCoCo report: {xml_path}", file=sys.stderr)
            return 1

        pct, covered, total = parse_instruction_coverage(xml_path)
        total_covered += covered
        total_all += total
        status = "Pass" if pct >= min_coverage else "Fail"
        rows.append((module, pct, status))

    overall_pct = (total_covered / total_all * 100.0) if total_all else 0.0
    overall_status = "Pass" if overall_pct >= min_coverage else "Fail"
    body = build_body(rows, overall_pct, overall_status, min_coverage, sha, run_url)

    summary_path = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary_path:
        with open(summary_path, "a", encoding="utf-8") as summary:
            summary.write(body.replace(MARKER, "").strip())
            summary.write("\n")

    existing_id = find_existing_comment(repo, pr_number_int, token)
    if existing_id:
        url = f"https://api.github.com/repos/{repo}/issues/comments/{existing_id}"
        api_request("PATCH", url, token, {"body": body})
        print(f"Updated PR comment {existing_id}")
    else:
        url = f"https://api.github.com/repos/{repo}/issues/{pr_number_int}/comments"
        api_request("POST", url, token, {"body": body})
        print("Created new PR coverage comment")

    if overall_pct < min_coverage:
        print(f"Overall coverage {overall_pct:.2f}% is below minimum {min_coverage}%", file=sys.stderr)
        return 1

    for module, pct, status in rows:
        if status == "Fail":
            print(f"{module} coverage {pct:.2f}% is below minimum {min_coverage}%", file=sys.stderr)
            return 1

    return 0


if __name__ == "__main__":
    sys.exit(main())
