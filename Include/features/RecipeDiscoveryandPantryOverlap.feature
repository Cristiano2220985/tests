Feature: Recipe Discovery

As a user
I want to search for recipes that match my pantry ingredients
So that I can prepare meals using the food I already have

Scenario: Search recipes using pantry ingredients

Given I have completed the onboarding
	And the pantry contains "Fettuccine" and "Parmesan"
	When I open the recipe discovery page
	And I tap the search recipes button
	Then I see a list of matching recipes
	And I close the AUT

Scenario: Show missing ingredients and pantry overlap

Given I have completed the onboarding
	And the pantry contains "Fettuccine"
	When I search for recipes
	And I open the recipe "Fettuccine Alfredo"
	Then I see the available ingredients
	And I see the missing ingredients
	And I see the ingredient overlap percentage
	And I close the AUT

Scenario: Prioritize recipes that use expiring ingredients

Given I have completed the onboarding
	And the pantry contains "Milk" expiring within 48 hours
	When I search for recipes
	Then recipes using "Milk" are shown with higher priority
	And I close the AUT

Scenario: Filter recipes by dietary restrictions

Given the active profile is "Vegan"
	When I search for recipes
	Then I do not see recipes containing animal products
	And I close the AUT