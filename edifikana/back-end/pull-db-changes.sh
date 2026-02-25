#!/bin/bash

# Script to pull database changes (RLS policies, schema, etc.) from remote Supabase
# and save them as migration files
#
# Usage: ./pull-db-changes.sh
# Run from: edifikana/back-end/
#
# Note: Assumes Docker is running with local Supabase instance

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Supabase Database Changes Puller${NC}"
echo -e "${BLUE}========================================${NC}\n"

# Check if supabase CLI is installed
if ! command -v supabase &> /dev/null; then
    echo -e "${RED}Error: Supabase CLI is not installed${NC}"
    echo -e "${YELLOW}Install it with: npm install -g supabase${NC}"
    echo -e "${YELLOW}Or visit: https://supabase.com/docs/guides/cli${NC}"
    exit 1
fi

echo -e "${BLUE}Pulling remote database changes...${NC}"
echo -e "${YELLOW}This will compare your remote database with local migrations${NC}\n"

# Pull remote changes and create migration file using the recommended command
if supabase db pull --local; then
    echo -e "\n${GREEN}✓${NC} Successfully pulled database changes!"

    # Check if any new migration files were created
    LATEST_MIGRATION=$(ls -t supabase/migrations/*.sql 2>/dev/null | head -n 1)

    if [ -n "$LATEST_MIGRATION" ]; then
        echo -e "${GREEN}✓${NC} New migration file created: ${BLUE}$LATEST_MIGRATION${NC}"
        echo -e "\n${YELLOW}Next steps:${NC}"
        echo -e "  1. Review the migration file: ${BLUE}$LATEST_MIGRATION${NC}"
        echo -e "  2. Test locally: ${BLUE}supabase db reset${NC}"
        echo -e "  3. Commit to git if changes look good"
    else
        echo -e "${YELLOW}No schema changes detected - your local migrations are up to date!${NC}"
    fi

    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${GREEN}Done!${NC}"
    echo -e "${BLUE}========================================${NC}"
else
    echo -e "\n${RED}✗ Failed to pull database changes${NC}"
    echo -e "${YELLOW}Common issues:${NC}"
    echo -e "  - Ensure Docker is running with Supabase containers"
    echo -e "  - Check that Supabase is accessible: ${BLUE}supabase status${NC}"
    exit 1
fi
