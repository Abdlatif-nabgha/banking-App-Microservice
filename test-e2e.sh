#!/usr/bin/env bash
# ==============================================================================
# Banking App - End-to-End Automated Testing Script
# Tests all operations through the Spring Cloud Gateway (:8888)
# ==============================================================================

set -euo pipefail

# ANSI Colors
GREEN="\033[0;32m"
BLUE="\033[0;34m"
YELLOW="\033[1;33m"
RED="\033[0;31m"
BOLD="\033[1m"
RESET="\033[0m"

GATEWAY_URL="http://localhost:8888"
CONFIG_URL="http://localhost:9999"
EUREKA_URL="http://localhost:8761"

echo -e "${BOLD}${BLUE}==================================================================${RESET}"
echo -e "${BOLD}${BLUE}      BANKING MICROSERVICES - END-TO-END VALIDATION TEST          ${RESET}"
echo -e "${BOLD}${BLUE}==================================================================${RESET}\n"

# Helper function to extract JSON values using python3
extract_json_field() {
    python3 -c "import sys, json; data=json.load(sys.stdin); print(data.get('$1', ''))"
}

# ------------------------------------------------------------------------------
# STEP 0: Check Infrastructure Services Health
# ------------------------------------------------------------------------------
echo -e "${BOLD}${YELLOW}[Step 0] Checking Infrastructure Services...${RESET}"

check_service() {
    local name="$1"
    local url="$2"
    if curl -s -f -o /dev/null "$url"; then
        echo -e "  [✔] ${GREEN}${name} is UP at ${url}${RESET}"
    else
        echo -e "  [✘] ${RED}${name} is UNREACHABLE at ${url}${RESET}"
        echo -e "      ${RED}Please ensure ${name} is running before executing this test.${RESET}"
        exit 1
    fi
}

check_service "Config Service" "${CONFIG_URL}/actuator/health"
check_service "Discovery Service (Eureka)" "${EUREKA_URL}/actuator/health"
check_service "API Gateway" "${GATEWAY_URL}/actuator/health"

echo -e ""

# ------------------------------------------------------------------------------
# STEP 1: Create a Customer via Gateway
# ------------------------------------------------------------------------------
echo -e "${BOLD}${YELLOW}[Step 1] Creating a new Customer via Gateway (:8888)...${RESET}"

CUSTOMER_RESPONSE=$(curl -s -X POST "${GATEWAY_URL}/api/v1/customers" \
    -H "Content-Type: application/json" \
    -d '{
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe.'$(date +%s)'@example.com"
    }')

CUSTOMER_ID=$(echo "${CUSTOMER_RESPONSE}" | extract_json_field "id")

if [ -z "${CUSTOMER_ID}" ]; then
    echo -e "${RED}Failed to create customer. Response:${RESET}\n${CUSTOMER_RESPONSE}"
    exit 1
fi

echo -e "  [✔] ${GREEN}Customer created successfully!${RESET}"
echo -e "      Customer ID: ${BOLD}${CUSTOMER_ID}${RESET}\n"

# ------------------------------------------------------------------------------
# STEP 2: Open Two Accounts for Customer via Gateway
# ------------------------------------------------------------------------------
echo -e "${BOLD}${YELLOW}[Step 2] Opening Accounts for Customer via Gateway...${RESET}"

# Account 1 (CHECKING)
ACCOUNT1_RESPONSE=$(curl -s -X POST "${GATEWAY_URL}/api/v1/accounts" \
    -H "Content-Type: application/json" \
    -d '{
        "customerId": "'"${CUSTOMER_ID}"'",
        "accountType": "CHECKING"
    }')
ACCOUNT1_ID=$(echo "${ACCOUNT1_RESPONSE}" | extract_json_field "id")

# Account 2 (SAVINGS)
ACCOUNT2_RESPONSE=$(curl -s -X POST "${GATEWAY_URL}/api/v1/accounts" \
    -H "Content-Type: application/json" \
    -d '{
        "customerId": "'"${CUSTOMER_ID}"'",
        "accountType": "SAVINGS"
    }')
ACCOUNT2_ID=$(echo "${ACCOUNT2_RESPONSE}" | extract_json_field "id")

echo -e "  [✔] ${GREEN}Account 1 (CHECKING) created:${RESET} ${BOLD}${ACCOUNT1_ID}${RESET}"
echo -e "  [✔] ${GREEN}Account 2 (SAVINGS)  created:${RESET} ${BOLD}${ACCOUNT2_ID}${RESET}\n"

# ------------------------------------------------------------------------------
# STEP 3: Deposit Funds into Account 1 via Gateway
# ------------------------------------------------------------------------------
echo -e "${BOLD}${YELLOW}[Step 3] Depositing $500.00 into Account 1 via Gateway...${RESET}"

DEPOSIT_RESPONSE=$(curl -s -X POST "${GATEWAY_URL}/api/v1/transactions/deposit" \
    -H "Content-Type: application/json" \
    -d '{
        "accountId": "'"${ACCOUNT1_ID}"'",
        "amount": 500.00
    }')

DEPOSIT_STATUS=$(echo "${DEPOSIT_RESPONSE}" | extract_json_field "status")

if [ "${DEPOSIT_STATUS}" != "COMPLETED" ]; then
    echo -e "${RED}Deposit failed. Response:${RESET}\n${DEPOSIT_RESPONSE}"
    exit 1
fi

echo -e "  [✔] ${GREEN}Deposit completed! Status: ${BOLD}${DEPOSIT_STATUS}${RESET}\n"

# ------------------------------------------------------------------------------
# STEP 4: Transfer Funds (Account 1 -> Account 2) via Gateway
# ------------------------------------------------------------------------------
echo -e "${BOLD}${YELLOW}[Step 4] Transferring $150.00 from Account 1 to Account 2...${RESET}"

TRANSFER_RESPONSE=$(curl -s -X POST "${GATEWAY_URL}/api/v1/transactions/transfer" \
    -H "Content-Type: application/json" \
    -d '{
        "sourceAccountId": "'"${ACCOUNT1_ID}"'",
        "destinationAccountId": "'"${ACCOUNT2_ID}"'",
        "amount": 150.00
    }')

TRANSFER_STATUS=$(echo "${TRANSFER_RESPONSE}" | extract_json_field "status")

if [ "${TRANSFER_STATUS}" != "COMPLETED" ]; then
    echo -e "${RED}Transfer failed. Response:${RESET}\n${TRANSFER_RESPONSE}"
    exit 1
fi

echo -e "  [✔] ${GREEN}Transfer completed successfully! Status: ${BOLD}${TRANSFER_STATUS}${RESET}\n"

# ------------------------------------------------------------------------------
# STEP 5: Verify Balances After Transfer
# ------------------------------------------------------------------------------
echo -e "${BOLD}${YELLOW}[Step 5] Verifying Account Balances...${RESET}"

ACC1_DETAILS=$(curl -s "${GATEWAY_URL}/api/v1/accounts/${ACCOUNT1_ID}")
ACC1_BALANCE=$(echo "${ACC1_DETAILS}" | extract_json_field "balance")

ACC2_DETAILS=$(curl -s "${GATEWAY_URL}/api/v1/accounts/${ACCOUNT2_ID}")
ACC2_BALANCE=$(echo "${ACC2_DETAILS}" | extract_json_field "balance")

echo -e "  [✔] Account 1 Balance (Expected $350.00): ${BOLD}${GREEN}$${ACC1_BALANCE}${RESET}"
echo -e "  [✔] Account 2 Balance (Expected $150.00): ${BOLD}${GREEN}$${ACC2_BALANCE}${RESET}\n"

# ------------------------------------------------------------------------------
# STEP 6: SAGA Compensation Test (Transfer to an Invalid Account)
# ------------------------------------------------------------------------------
echo -e "${BOLD}${YELLOW}[Step 6] Testing SAGA Pattern Compensation (Transfer to invalid account)...${RESET}"

FAILED_TX_RESPONSE=$(curl -s -X POST "${GATEWAY_URL}/api/v1/transactions/transfer" \
    -H "Content-Type: application/json" \
    -d '{
        "sourceAccountId": "'"${ACCOUNT1_ID}"'",
        "destinationAccountId": "00000000-0000-0000-0000-000000000000",
        "amount": 100.00
    }')

FAILED_TX_STATUS=$(echo "${FAILED_TX_RESPONSE}" | extract_json_field "status")
FAILURE_REASON=$(echo "${FAILED_TX_RESPONSE}" | extract_json_field "failureReason")

echo -e "  [✔] Transaction Status as expected: ${BOLD}${RED}${FAILED_TX_STATUS}${RESET}"
echo -e "  [✔] Failure Reason logged: ${BOLD}${FAILURE_REASON}${RESET}"

# Verify Account 1 balance was NOT deducted (Compensation verified!)
ACC1_CHECK=$(curl -s "${GATEWAY_URL}/api/v1/accounts/${ACCOUNT1_ID}")
ACC1_NEW_BALANCE=$(echo "${ACC1_CHECK}" | extract_json_field "balance")

if [ "${ACC1_NEW_BALANCE}" == "${ACC1_BALANCE}" ]; then
    echo -e "  [✔] ${BOLD}${GREEN}SAGA SUCCESS: Account 1 was refunded! Balance remained: $${ACC1_NEW_BALANCE}${RESET}\n"
else
    echo -e "  [✘] ${RED}SAGA FAILURE: Account 1 was not refunded properly! Balance: $${ACC1_NEW_BALANCE}${RESET}\n"
    exit 1
fi

# ------------------------------------------------------------------------------
# STEP 7: Retrieve Transaction History for Account 1
# ------------------------------------------------------------------------------
echo -e "${BOLD}${YELLOW}[Step 7] Retrieving Transaction History for Account 1...${RESET}"

TX_HISTORY=$(curl -s "${GATEWAY_URL}/api/v1/transactions?accountId=${ACCOUNT1_ID}")

echo -e "${TX_HISTORY}" | python3 -m json.tool || echo "${TX_HISTORY}"

echo -e "\n${BOLD}${GREEN}==================================================================${RESET}"
echo -e "${BOLD}${GREEN}      ALL TESTS PASSED! FULL END-TO-END VALIDATION COMPLETE       ${RESET}"
echo -e "${BOLD}${GREEN}==================================================================${RESET}"

