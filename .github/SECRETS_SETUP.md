# GitHub Secrets Configuration Guide

This document explains how to configure GitHub Secrets for automated releases.

## 📋 Required Secrets

The following secrets must be configured in your GitHub repository for the CI/CD workflow to function:

| Secret Name | Description | Required |
|-------------|-------------|----------|
| `KEYSTORE_FILE` | Base64-encoded keystore file | ✅ Yes |
| `KEYSTORE_PASSWORD` | Password for the keystore | ✅ Yes |
| `KEY_ALIAS` | Alias name for the signing key | ✅ Yes |
| `KEY_PASSWORD` | Password for the signing key | ✅ Yes |

## 🔐 Step-by-Step Setup

### 1. Generate a Keystore

If you don't have a keystore yet, run the generation script:

```bash
./generate-keystore.sh
```

This will create:
- `keystore.jks` - Your signing keystore
- `keystore.properties` - Local build configuration

**IMPORTANT:** Keep these files secure and never commit them to git!

### 2. Encode the Keystore to Base64

The keystore must be base64-encoded to store it as a GitHub secret.

#### On Linux/macOS:

```bash
base64 -i keystore.jks | pbcopy
```

Or save to a file:

```bash
base64 -i keystore.jks > keystore.txt
```

#### On Windows (Git Bash):

```bash
base64 keystore.jks > keystore.txt
```

#### On Windows (PowerShell):

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("keystore.jks")) | Out-File keystore.txt
```

### 3. Add Secrets to GitHub

1. Go to your GitHub repository
2. Click **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add each secret:

#### KEYSTORE_FILE

- **Name:** `KEYSTORE_FILE`
- **Value:** Paste the entire base64 string from keystore.txt
- Click **Add secret**

#### KEYSTORE_PASSWORD

- **Name:** `KEYSTORE_PASSWORD`
- **Value:** The keystore password you set during generation
- Click **Add secret**

#### KEY_ALIAS

- **Name:** `KEY_ALIAS`
- **Value:** The key alias (default: `kiro-sonnet-player`)
- Click **Add secret**

#### KEY_PASSWORD

- **Name:** `KEY_PASSWORD`
- **Value:** The key password you set during generation
- Click **Add secret**

### 4. Verify Configuration

After adding all secrets, you should see:

```
✅ KEYSTORE_FILE
✅ KEYSTORE_PASSWORD
✅ KEY_ALIAS
✅ KEY_PASSWORD
```

## 🚀 Testing the Workflow

To test the automated release workflow:

1. Make a commit with a conventional commit message:
   ```bash
   git commit -m "feat: add new feature"
   ```

2. Push to the main branch:
   ```bash
   git push origin main
   ```

3. Go to **Actions** tab in GitHub to watch the workflow run

4. If successful, a new release will be created with the signed APK

## 🔍 Troubleshooting

### Error: "KEYSTORE_FILE secret is not set"

- Verify the secret name is exactly `KEYSTORE_FILE` (case-sensitive)
- Check that the base64 content was copied completely

### Error: "Incorrect keystore password"

- Verify `KEYSTORE_PASSWORD` matches the password used during generation
- Check for any trailing spaces in the secret value

### Error: "Alias not found"

- Verify `KEY_ALIAS` matches the alias used during keystore generation
- Default alias is `kiro-sonnet-player`

### Error: "Failed to decode keystore"

- The base64 encoding may be corrupted
- Re-encode the keystore and update the secret

### Workflow doesn't trigger

- Ensure you're pushing to the `main` branch
- Check the workflow file is in `.github/workflows/release.yml`
- Verify the workflow file syntax is correct

## 🔒 Security Best Practices

### Keystore Security

1. **Never commit** keystore files to version control
2. **Backup** your keystore in a secure location
3. **Use strong passwords** (minimum 12 characters recommended)
4. **Store credentials** in a password manager
5. **Limit access** to keystore files and GitHub secrets

### GitHub Secrets Security

- Secrets are encrypted and never exposed in logs
- Only workflows in the repository can access secrets
- Secrets are not passed to workflows triggered by pull requests from forks
- Regularly rotate credentials if team members change

### Recovery Plan

If you lose your keystore:

- **You cannot update your app** on devices that have the old version
- You must create a **new app** with a different package name
- Users must **uninstall and reinstall** the app

**This is why backups are critical!**

## 📦 Backup Recommendations

1. **Primary Backup:** Secure cloud storage (encrypted)
2. **Secondary Backup:** External hard drive (encrypted)
3. **Tertiary Backup:** Password manager with file attachment capability

Store along with keystore:
- `keystore.jks`
- `keystore.properties`
- A text file with all passwords and the key alias
- This README for recovery instructions

## 🔄 Rotating Secrets

If you need to rotate your keystore (not recommended unless compromised):

1. Generate a new keystore
2. Update all GitHub secrets
3. Release a new version with the new signature
4. Users will need to uninstall and reinstall

**Note:** Android will treat this as a different app.

## 📞 Support

If you encounter issues:

1. Check the [workflow logs](https://github.com/yourusername/kiro-sonnet-player/actions)
2. Review the [troubleshooting section](#-troubleshooting)
3. Open an [issue](https://github.com/yourusername/kiro-sonnet-player/issues) with:
   - Error message (redact sensitive info)
   - Steps you've tried
   - Workflow run link

---

**Last Updated:** 2026-08-16
