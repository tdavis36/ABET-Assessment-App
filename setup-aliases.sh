#!/bin/bash
# Script to set up database command aliases for Linux/macOS
# This adds aliases to your .bashrc or .zshrc file

# Text colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Get the project directory
PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

echo -e "${GREEN}Setting up database command aliases...${NC}"

# Detect shell
if [ -n "$ZSH_VERSION" ]; then
    SHELL_TYPE="zsh"
    SHELL_RC="$HOME/.zshrc"
elif [ -n "$BASH_VERSION" ]; then
    SHELL_TYPE="bash"
    SHELL_RC="$HOME/.bashrc"
    # For macOS, check if we need to use .bash_profile
    if [[ "$OSTYPE" == "darwin"* ]] && [ -f "$HOME/.bash_profile" ]; then
        SHELL_RC="$HOME/.bash_profile"
    fi
else
    echo -e "${YELLOW}Unsupported shell. Please manually add aliases to your shell's configuration file.${NC}"
    exit 1
fi

# Define the aliases content
ALIASES_CONTENT="
# Database management aliases for the project
alias dbstart='$PROJECT_DIR/db-maven.sh start'
alias dbstop='$PROJECT_DIR/db-maven.sh stop'
alias dbsync='$PROJECT_DIR/db-maven.sh sync'
alias dbrestart='$PROJECT_DIR/db-maven.sh restart'

# Print usage info when shell starts
echo -e \"${CYAN}Database management commands available:${NC}\"
echo -e \"${CYAN}  dbstart   - Start the database container${NC}\"
echo -e \"${CYAN}  dbstop    - Stop the database container${NC}\"
echo -e \"${CYAN}  dbsync    - Sync database schema${NC}\"
echo -e \"${CYAN}  dbrestart - Restart database and sync schema${NC}\"
echo -e \"${CYAN}Example: dbsync --db-name=custom_db${NC}\"
"

# Check if aliases already exist
if grep -q "Database management aliases for the project" "$SHELL_RC"; then
    echo -e "${YELLOW}Aliases are already defined in $SHELL_RC${NC}"
    echo -e "${YELLOW}Updating existing aliases...${NC}"

    # Create a temporary file
    TEMP_FILE=$(mktemp)

    # Remove existing aliases section and write to temp file
    sed '/# Database management aliases for the project/,/Example:/d' "$SHELL_RC" > "$TEMP_FILE"

    # Add the updated aliases to the end
    echo "$ALIASES_CONTENT" >> "$TEMP_FILE"

    # Replace the original file
    mv "$TEMP_FILE" "$SHELL_RC"
else
    # Append aliases to shell configuration
    echo "$ALIASES_CONTENT" >> "$SHELL_RC"
    echo -e "${GREEN}Aliases added to $SHELL_RC${NC}"
fi

echo -e "${GREEN}To use the aliases in the current session, run: source $SHELL_RC${NC}"
echo -e "${GREEN}Or start a new terminal session.${NC}"

# Ask if user wants to apply changes to current session
read -p "Do you want to apply these changes to your current session? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    source "$SHELL_RC"
    echo -e "${GREEN}Aliases are now available in the current session!${NC}"
fi

# Make the aliases executable
chmod +x "$PROJECT_DIR/db-maven.sh"