Feature: Recipe Book
  As a user
  I want to save and organize recipes
  So that I can access them later

  Scenario: Save a recipe to the recipe book
    Given I have completed the onboarding
    And I am viewing the recipe "Fettuccine Alfredo"
    When I tap the "Save Recipe" button
    Then "Fettuccine Alfredo" appears in my recipe book
    And I close the AUT

  Scenario: Create a collection
    Given I have completed the onboarding
    When I open the recipe book
    And I create a collection named "Italian Favorites"
    Then I see the collection "Italian Favorites"
    And I close the AUT

  Scenario: Add a recipe to a collection
    Given the collection "Italian Favorites" exists
    And the recipe "Fettuccine Alfredo" is saved
    When I add the recipe to the collection
    Then "Fettuccine Alfredo" appears in "Italian Favorites"
    And I close the AUT

  Scenario: Remove a recipe from a collection
    Given the recipe "Fettuccine Alfredo" belongs to the collection "Italian Favorites"
    When I remove the recipe from the collection
    Then I do not see "Fettuccine Alfredo" in "Italian Favorites"
    And I close the AUT
