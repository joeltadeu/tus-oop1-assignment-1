Feature: Borrow and return books flow for member 1

  Background:
    # baseUrl comes from karate-config.js
    * url baseUrl
    * header Content-Type = 'application/json'

  @library-management
  Scenario: Borrow items, save loan id, return items, assert closed + returnedDate set
    # 1) Borrow books
    Given path 'v1', 'members', 1, 'loans'
    And request { items: [1, 2, 4] }
    When method post
    Then status 200
    And match response.status == 'OPEN'
    * def loanId = response.id
    * print 'loanId ->', loanId

    # 2) Retrieve all loans for member 1 and assert the loan appears
    Given path 'v1', 'members', 1, 'loans'
    When method get
    Then status 200
    And match response[*].loanId contains loanId

    # 3) Return the borrowed items (call returns endpoint)
    Given path 'v1', 'loans', loanId, 'returns'
    And request { items: [1, 2, 4] }
    When method post
    Then status 200

    And match response.status == 'CLOSED'
    And match each response.items[*].returnedDate == '#notnull'

    # 4) Get the loan by id and double-check final state
    Given path 'v1', 'loans', loanId
    When method get
    Then status 200
    And match response.status == 'CLOSED'
    And match each response.items[*].returnedDate == '#notnull'

  @library-management
  Scenario: Partial return flow - borrow items 3 and 5, return item 3, then item 5
    # 1) Borrow books with ids 3 and 5
    Given path 'v1', 'members', 1, 'loans'
    And request { items: [3, 5] }
    When method post
    Then status 200
    And match response.status == 'OPEN'
    * def loanId2 = response.id
    * print 'loanId2 ->', loanId2

    # 2) Ensure the new loan appears in the member's loans list
    Given path 'v1', 'members', 1, 'loans'
    When method get
    Then status 200
    And match response[*].loanId contains loanId2

    # 3) Return only item 3
    Given path 'v1', 'loans', loanId2, 'returns'
    And request { items: [3] }
    When method post
    Then status 200

    # 4) Get the loan and check status is still OPEN and only item 3 has returnedDate
    Given path 'v1', 'loans', loanId2
    When method get
    Then status 200
    And match response.status == 'OPEN'

    * def item3List = karate.jsonPath(response.items, "$[?(@.id == 3)]")
    And match item3List == '#[1]'
    And match item3List[0].returnedDate == '#notnull'

    * def item5List = karate.jsonPath(response.items, "$[?(@.id == 5)]")
    And match item3List == '#[1]'
    And match item5List[0].returnedDate == '#null'

    # 5) Return the remaining item 5
    Given path 'v1', 'loans', loanId2, 'returns'
    And request { items: [5] }
    When method post
    Then status 200

    # 6) Get the loan and assert it's CLOSED and every item has a returnedDate
    Given path 'v1', 'loans', loanId2
    When method get
    Then status 200
    And match response.status == 'CLOSED'
    And match each response.items[*].returnedDate == '#notnull'
