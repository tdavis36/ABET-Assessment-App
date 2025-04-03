#!/bin/bash
# Script to set up Git hooks for the project

# Text colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Get project root
PROJECT_ROOT=$(git rev-parse --show-toplevel)
cd "$PROJECT_ROOT" || exit

# Create hooks directory if it doesn't exist
mkdir -p .git/hooks

# Create post-commit hook
echo -e "${YELLOW}Creating post-commit hook...${NC}"
cat > .git/hooks/post-commit << 'EOF'
#!/bin/bash
# Git post-commit hook to run database migrations after commit

# Get project root directory
PROJECT_ROOT=$(git rev-parse --show-toplevel)
cd "$PROJECT_ROOT"

# Check if any migration files were changed in this commit
MIGRATION_FILES_CHANGED=$(git diff-tree --no-commit-id --name-only -r HEAD | grep -E 'src/main/resources/db/migration/.*\.sql$')

if [ -n "$MIGRATION_FILES_CHANGED" ]; then
  echo "Migration files changed in this commit. Running migrations..."

  # Determine OS and run appropriate script
  if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    # Windows environment
    ./db-maven.bat migrate
  else
    # Unix-like environment
    ./db-maven.sh migrate
  fi

  # Check if migrations were successful
  if [ $? -eq 0 ]; then
    echo "✅ Migrations executed successfully"
  else
    echo "❌ Migration failed. Please fix issues and run migrations manually."
  fi
else
  echo "No migration files changed. Skipping migrations."
fi
EOF

# Make hooks executable
chmod +x .git/hooks/post-commit

echo -e "${GREEN}Git hooks set up successfully!${NC}"
echo -e "${YELLOW}Now database migrations will run automatically after commits that change migration files.${NC}"