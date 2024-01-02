# Currency
This project provides APIs for currency exchange information, leveraging the Coindesk Bitcoin Price Index (BPI). Users can retrieve the latest Coindesk data, transform it, and perform various operations on currency information.

## Environment
- Java 8
- Spring Boot 2.5.4

## Endpoint Descriptions

### 1. Get Coindesk Data
- Endpoint: /v1/getCoindesk
- Method: GET
- Description: Get the latest Coindesk data.
  
### 2. Transform Coindesk Data
- Endpoint: /v1/transformCoindesk
- Method: GET
- Description: Transform and return Coindesk data.
  
### 3. Get All Currency Information
- Endpoint: /v1/getAllCurrencies
- Method: GET
- Description: Get a list of all currency information.
  
### 4. Get Specific Currency Information
- Endpoint: /v1/getCurrency/{ccyCode}
- Method: GET
- Description: Get detailed information about a specific currency.
- Parameters:
  - {ccyCode}: Currency code

### 5. Add Currency Information
- Endpoint: /v1/addCurrency
- Method: POST
- Description: Add new currency information.
- Parameters:
  - Request body containing a JSON object with currency information.

### 6. Update Currency Information
- Endpoint: /v1/updateCurrency
- Method: PUT
- Description: Update existing currency information.
- Parameters:
  - Request body containing a JSON object with updated currency information.

### 7. Delete Currency Information
- Endpoint: /v1/deleteCurrency/{ccyCode}
- Method: DELETE
- Description: Delete information about a specific currency.
- Parameters:
  - {ccyCode}: Currency code
