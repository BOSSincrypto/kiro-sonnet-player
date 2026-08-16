# 🤖 GitHub Actions CI/CD Documentation

Complete documentation for the automated CI/CD workflows in this project.

## 📋 Overview

This project uses GitHub Actions for automated:
- ✅ Continuous Integration (CI) - Build and test on pull requests
- 🚀 Continuous Deployment (CD) - Automated releases with semantic versioning
- 📦 APK signing and distribution

## 🔄 Workflows

### 1. Release Workflow (`.github/workflows/release.yml`)

**Triggers:**
- Push to `main` branch
- Manual dispatch via Actions tab

**What it does:**
1. ✅ Runs lint checks
2. 📊 Determines version from commit messages (semantic versioning)
3. 🔨 Builds signed release APK
4. ✍️ Signs APK with release keystore
5. 📝 Generates changelog from commits
6. 🚀 Creates GitHub Release
7. 📦 Uploads signed APK to release
8. 🗄️ Archives build artifacts

**Secrets Required:**
- `KEYSTORE_FILE` - Base64-encoded keystore
- `KEYSTORE_PASSWORD` - Keystore password
- `KEY_ALIAS` - Key alias name
- `KEY_PASSWORD` - Key password

See [SECRETS_SETUP.md](SECRETS_SETUP.md) for configuration details.

**Semantic Versioning:**

| Commit Type | Example | Version Bump |
|-------------|---------|--------------|
| Breaking | `feat!: redesign API` | `1.0.0` → `2.0.0` |
| Feature | `feat: add shuffle mode` | `1.0.0` → `1.1.0` |
| Fix | `fix: playback crash` | `1.0.0` → `1.0.1` |
| Other | `docs: update readme` | `1.0.0` → `1.0.1` |

**Version Code Calculation:**
```
versionCode = (major × 10000) + (minor × 100) + patch

Examples:
v1.0.0  → 10000
v1.2.3  → 10203
v2.0.0  → 20000
```

### 2. Pull Request Checks (`.github/workflows/pr-checks.yml`)

**Triggers:**
- Pull requests to `main` or `develop` branches

**Jobs:**

#### Lint Job
- Runs Android lint checks
- Uploads lint report as artifact
- Fails PR if critical issues found

#### Build Job
- Builds debug APK
- Verifies project compiles successfully
- Uploads debug APK as artifact

#### Test Job
- Runs unit tests
- Uploads test results and reports
- Must pass for PR to be mergeable

**Branch Protection:**

Recommended settings for `main` branch:
1. Go to **Settings → Branches → Branch protection rules**
2. Add rule for `main`
3. Enable:
   - ✅ Require status checks to pass before merging
   - ✅ Require branches to be up to date before merging
   - Select required checks:
     - `lint`
     - `build`
     - `test`

## 🔧 Workflow Customization

### Change Release Branch

Edit `.github/workflows/release.yml`:

```yaml
on:
  push:
    branches:
      - main          # Change this
      - production    # Or add multiple branches
```

### Adjust Versioning Strategy

Modify version determination in release workflow:

```yaml
- name: Determine version
  id: version
  run: |
    # Customize version bump logic here
```

### Add Additional Checks

Add jobs to `pr-checks.yml`:

```yaml
coverage:
  name: Code Coverage
  runs-on: ubuntu-latest
  steps:
    - name: Run tests with coverage
      run: ./gradlew testDebugUnitTestCoverage
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
```

### Change Signing Strategy

For alternative signing approaches, modify release workflow:

```yaml
- name: Sign APK
  run: |
    # Your custom signing commands
```

## 📦 Artifacts

### Release Artifacts

**Retention:** 30 days

Includes:
- Signed release APK
- ProGuard mapping files (for crash deobfuscation)
- R8 optimization reports

**Access:**
1. Go to **Actions** tab
2. Select workflow run
3. Scroll to **Artifacts** section
4. Download artifacts

### PR Artifacts

**Retention:** 7 days (default)

Includes:
- Debug APK
- Lint reports
- Test results
- Test coverage reports

## 🚀 Manual Release

### Via GitHub Actions

1. Go to **Actions** tab
2. Select **Android Release Build** workflow
3. Click **Run workflow**
4. Select branch
5. Click **Run workflow**

### Via Command Line (GitHub CLI)

```bash
# Trigger release workflow
gh workflow run release.yml

# Check status
gh run list --workflow=release.yml
```

## 📊 Monitoring Workflows

### View Workflow Runs

```bash
# List recent runs
gh run list

# View specific run
gh run view <run-id>

# Watch live run
gh run watch
```

### Email Notifications

Configure in **Settings → Notifications → Actions**:
- ✅ Send notifications for failed workflows
- ✅ Send notifications for workflow runs requiring approval

## 🔍 Debugging Workflows

### Enable Debug Logging

Add secrets to your repository:

```
ACTIONS_STEP_DEBUG = true
ACTIONS_RUNNER_DEBUG = true
```

Then re-run the workflow.

### View Job Logs

1. Go to **Actions** tab
2. Select failed workflow run
3. Click on failed job
4. Expand failed step
5. Review logs

### Test Locally with Act

Install [act](https://github.com/nektos/act):

```bash
# Install act
brew install act  # macOS
# or
choco install act  # Windows

# Run workflow locally
act push
```

## 🔐 Security Best Practices

### Secrets Management

✅ **Do:**
- Store all sensitive data as GitHub Secrets
- Use environment-specific secrets
- Rotate secrets periodically
- Limit secret scope to necessary workflows

❌ **Don't:**
- Hardcode secrets in workflows
- Echo secrets in logs
- Share secrets across repositories unnecessarily
- Store secrets in code or config files

### Workflow Permissions

Limit permissions in workflow files:

```yaml
permissions:
  contents: write  # Only what's needed
  # Not: permissions: write-all
```

### Dependency Security

Keep actions up to date:

```yaml
# ✅ Good - Pinned to specific version
- uses: actions/checkout@v4

# ⚠️ Acceptable - Major version pinning
- uses: actions/setup-java@v4

# ❌ Bad - Using latest (unpredictable)
- uses: actions/checkout@main
```

## 📈 Performance Optimization

### Gradle Caching

Already implemented:

```yaml
- name: Cache Gradle dependencies
  uses: actions/cache@v4
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
    key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}
```

### Build Time Optimization

Current optimizations:
- ✅ Gradle build caching enabled
- ✅ Parallel execution
- ✅ Dependency caching
- ✅ Incremental builds

Expected build times:
- **First build:** 8-12 minutes
- **Cached build:** 3-5 minutes
- **No changes:** 1-2 minutes

### Matrix Builds

For testing on multiple API levels:

```yaml
strategy:
  matrix:
    api-level: [29, 30, 34]
```

## 📝 Conventional Commits

For automated changelog and versioning:

### Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

| Type | Description | Version Bump |
|------|-------------|--------------|
| `feat` | New feature | Minor |
| `fix` | Bug fix | Patch |
| `feat!` | Breaking change | Major |
| `docs` | Documentation | Patch |
| `style` | Code style | Patch |
| `refactor` | Code refactoring | Patch |
| `perf` | Performance | Patch |
| `test` | Tests | Patch |
| `chore` | Maintenance | Patch |

### Examples

```bash
# Minor version bump
git commit -m "feat(player): add shuffle mode"

# Patch version bump
git commit -m "fix(ui): correct track duration display"

# Major version bump
git commit -m "feat!: redesign playback API

BREAKING CHANGE: PlaybackService now requires PlaybackConfig"

# With scope and body
git commit -m "perf(player): optimize audio buffer management

Reduced memory usage by 30% by implementing buffer pooling.
Improved playback stability on low-end devices."
```

## 🎯 Changelog Generation

Changelog is automatically generated from commits:

### Format

```markdown
## What's Changed

### ✨ Features
- feat(player): add shuffle mode (#123)
- feat(ui): implement dark theme (#124)

### 🐛 Bug Fixes
- fix(player): resolve playback crash (#125)
- fix(ui): correct layout alignment (#126)

### 🔧 Other Changes
- docs: update installation guide (#127)
- chore: update dependencies (#128)
```

## 🔄 Workflow Status Badges

Add badges to README:

```markdown
[![Release](https://github.com/yourusername/kiro-sonnet-player/actions/workflows/release.yml/badge.svg)](https://github.com/yourusername/kiro-sonnet-player/actions/workflows/release.yml)

[![PR Checks](https://github.com/yourusername/kiro-sonnet-player/actions/workflows/pr-checks.yml/badge.svg)](https://github.com/yourusername/kiro-sonnet-player/actions/workflows/pr-checks.yml)
```

## 📞 Support

### Workflow Issues

If workflows fail:

1. Check workflow logs in Actions tab
2. Verify all secrets are configured
3. Ensure branch protection rules are correct
4. Check for syntax errors in YAML
5. Open an issue if problem persists

### Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Workflow Syntax](https://docs.github.com/en/actions/reference/workflow-syntax-for-github-actions)
- [GitHub Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)

---

**Last Updated:** 2026-08-16
