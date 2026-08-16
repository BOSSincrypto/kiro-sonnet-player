#!/bin/bash

# Keystore Generation Script for Kiro Sonnet Player
# This script generates a release keystore for signing Android APKs

set -e

echo "=========================================="
echo "  Kiro Sonnet Player Keystore Generator"
echo "=========================================="
echo ""

# Default values
DEFAULT_ALIAS="kiro-sonnet-player"
DEFAULT_KEYSTORE_FILE="keystore.jks"
DEFAULT_VALIDITY=10000

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if keystore already exists
if [ -f "$DEFAULT_KEYSTORE_FILE" ]; then
    echo -e "${YELLOW}Warning: $DEFAULT_KEYSTORE_FILE already exists!${NC}"
    read -p "Do you want to overwrite it? (yes/no): " overwrite
    if [ "$overwrite" != "yes" ]; then
        echo "Aborting. Existing keystore preserved."
        exit 0
    fi
    echo ""
fi

# Get keystore information
echo "Please provide the following information:"
echo ""

read -p "Key alias [$DEFAULT_ALIAS]: " KEY_ALIAS
KEY_ALIAS=${KEY_ALIAS:-$DEFAULT_ALIAS}

read -sp "Keystore password (min 6 characters): " KEYSTORE_PASSWORD
echo ""

if [ ${#KEYSTORE_PASSWORD} -lt 6 ]; then
    echo -e "${RED}Error: Password must be at least 6 characters long${NC}"
    exit 1
fi

read -sp "Confirm keystore password: " KEYSTORE_PASSWORD_CONFIRM
echo ""

if [ "$KEYSTORE_PASSWORD" != "$KEYSTORE_PASSWORD_CONFIRM" ]; then
    echo -e "${RED}Error: Passwords do not match${NC}"
    exit 1
fi

read -sp "Key password (press Enter to use same as keystore): " KEY_PASSWORD
echo ""

if [ -z "$KEY_PASSWORD" ]; then
    KEY_PASSWORD="$KEYSTORE_PASSWORD"
fi

echo ""
echo "Certificate information (press Enter to skip optional fields):"

read -p "First and Last Name [Kiro Sonnet Player]: " DNAME_CN
DNAME_CN=${DNAME_CN:-"Kiro Sonnet Player"}

read -p "Organizational Unit (e.g., Development) []: " DNAME_OU

read -p "Organization (e.g., Your Company) []: " DNAME_O

read -p "City or Locality []: " DNAME_L

read -p "State or Province []: " DNAME_ST

read -p "Country Code (2 letters, e.g., US) []: " DNAME_C

# Build the distinguished name
DNAME="CN=$DNAME_CN"
[ -n "$DNAME_OU" ] && DNAME="$DNAME, OU=$DNAME_OU"
[ -n "$DNAME_O" ] && DNAME="$DNAME, O=$DNAME_O"
[ -n "$DNAME_L" ] && DNAME="$DNAME, L=$DNAME_L"
[ -n "$DNAME_ST" ] && DNAME="$DNAME, ST=$DNAME_ST"
[ -n "$DNAME_C" ] && DNAME="$DNAME, C=$DNAME_C"

echo ""
read -p "Validity in days [$DEFAULT_VALIDITY]: " VALIDITY
VALIDITY=${VALIDITY:-$DEFAULT_VALIDITY}

echo ""
echo "=========================================="
echo "Generating keystore with the following settings:"
echo "----------------------------------------"
echo "Keystore file: $DEFAULT_KEYSTORE_FILE"
echo "Key alias: $KEY_ALIAS"
echo "Distinguished Name: $DNAME"
echo "Validity: $VALIDITY days"
echo "=========================================="
echo ""

# Generate the keystore
keytool -genkeypair \
    -alias "$KEY_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity "$VALIDITY" \
    -keystore "$DEFAULT_KEYSTORE_FILE" \
    -storepass "$KEYSTORE_PASSWORD" \
    -keypass "$KEY_PASSWORD" \
    -dname "$DNAME" \
    -storetype JKS

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✓ Keystore generated successfully!${NC}"
    echo ""

    # Create keystore.properties file
    PROPERTIES_FILE="keystore.properties"
    cat > "$PROPERTIES_FILE" <<EOF
storeFile=$(pwd)/$DEFAULT_KEYSTORE_FILE
storePassword=$KEYSTORE_PASSWORD
keyAlias=$KEY_ALIAS
keyPassword=$KEY_PASSWORD
EOF

    echo -e "${GREEN}✓ Created $PROPERTIES_FILE${NC}"
    echo ""

    # Display keystore information
    echo "=========================================="
    echo "Keystore Information:"
    echo "=========================================="
    keytool -list -v -keystore "$DEFAULT_KEYSTORE_FILE" -storepass "$KEYSTORE_PASSWORD" -alias "$KEY_ALIAS"

    echo ""
    echo "=========================================="
    echo -e "${GREEN}Setup Complete!${NC}"
    echo "=========================================="
    echo ""
    echo "IMPORTANT: Save these credentials securely!"
    echo ""
    echo "Files created:"
    echo "  1. $DEFAULT_KEYSTORE_FILE - Your signing keystore (keep secure!)"
    echo "  2. $PROPERTIES_FILE - Build configuration (keep secure!)"
    echo ""
    echo "These files are ignored by git (.gitignore)."
    echo ""
    echo "For GitHub Actions, you need to:"
    echo "  1. Encode keystore to base64:"
    echo "     base64 $DEFAULT_KEYSTORE_FILE > keystore.txt"
    echo ""
    echo "  2. Add GitHub Secrets:"
    echo "     - KEYSTORE_FILE: (contents of keystore.txt)"
    echo "     - KEYSTORE_PASSWORD: $KEYSTORE_PASSWORD"
    echo "     - KEY_ALIAS: $KEY_ALIAS"
    echo "     - KEY_PASSWORD: (the key password you set)"
    echo ""
    echo -e "${YELLOW}Keep your keystore and passwords safe!${NC}"
    echo -e "${YELLOW}If you lose them, you cannot update your app!${NC}"
    echo ""

else
    echo ""
    echo -e "${RED}✗ Failed to generate keystore${NC}"
    exit 1
fi
