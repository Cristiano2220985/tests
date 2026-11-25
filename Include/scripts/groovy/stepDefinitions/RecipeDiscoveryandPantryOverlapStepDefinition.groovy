package stepDefinitions

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.openqa.selenium.WebElement

import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * US-19: Recipe discovery and pantry overlap.
 * "I have completed the onboarding" is defined in DashboardFoodTrackingAndPantryItemsStepDefinition
 * and "I close the AUT" in OnboardingProfileCreationAndSwitchingStepDefinition.
 */
class RecipeDiscoveryandPantryOverlapStepDefinition {

	private static final int TIMEOUT = 5
	// The search calls TheMealDB (external API), so the results can take a while.
	private static final int SEARCH_TIMEOUT = 30

	private final OnboardingProfileCreationAndSwitchingStepDefinition onboarding = new OnboardingProfileCreationAndSwitchingStepDefinition()

	// Given: set up the context ------------------------------------------------

	@Given("the pantry contains {string}")
	def pantryContains(String itemName) {
		addItem(itemName, LocalDate.now().plusDays(30))
	}

	@Given("the pantry contains {string} and {string}")
	def pantryContainsTwo(String firstItem, String secondItem) {
		pantryContains(firstItem)
		pantryContains(secondItem)
	}

	@Given("the pantry contains {string} expiring within {int} hours")
	def pantryContainsExpiring(String itemName, Integer hours) {
		// The app treats "today" and "tomorrow" as expiring (within 48 hours).
		int days = Math.max(0, Math.min(1, (int) (hours / 24) - 1))
		addItem(itemName, LocalDate.now().plusDays(days))
	}

	@Given("the active profile is {string}")
	def activeProfileIs(String lifestyle) {
		// Creates a profile named after the dietary lifestyle and selects that lifestyle.
		onboarding.startApplicationForTheFirstTime()
		onboarding.tapButton('Get started')
		onboarding.insertTextInField(lifestyle, 'Profile name')
		onboarding.tapButton('Next')
		onboarding.selectDietaryLifestyle(lifestyle)
		onboarding.tapButton('Next')
		onboarding.tapButton('Finish setup')
		onboarding.verifyDashboard()
		onboarding.verifyActiveProfile(lifestyle)
	}

	// When: perform the actions ------------------------------------------------

	@When("I open the recipe discovery page")
	def openRecipeDiscoveryPage() {
		// The link exists in the sidebar (desktop) and in the bottom bar (mobile): click the visible one.
		WebElement link = WebUI.findWebElements(object('a_Recipes nav'), TIMEOUT).find { it.isDisplayed() }
		assert link != null : 'The Recipes navigation link is not visible'
		WebUI.executeJavaScript('arguments[0].click()', [link])
		WebUI.waitForElementVisible(object('div_Recipe discovery'), TIMEOUT)
	}

	@When("I tap the search recipes button")
	def tapSearchRecipes() {
		WebUI.waitForElementClickable(object('button_Search recipes'), TIMEOUT)
		WebUI.click(object('button_Search recipes'))
		WebUI.waitForElementVisible(object('section_Search results'), SEARCH_TIMEOUT)
	}

	@When("I search for recipes")
	def searchForRecipes() {
		openRecipeDiscoveryPage()
		tapSearchRecipes()
	}

	@When("I open the recipe {string}")
	def openRecipe(String recipeName) {
		WebUI.waitForElementClickable(object('button_Recipe card title', recipeName), TIMEOUT)
		WebUI.click(object('button_Recipe card title', recipeName))
		WebUI.waitForElementVisible(object('section_Recipe detail', recipeName), TIMEOUT)
	}

	// Then: verify the expected outcomes ---------------------------------------

	@Then("I see a list of matching recipes")
	def verifyMatchingRecipes() {
		WebUI.verifyElementVisible(object('section_Search results'))
		assert recipeCards().size() > 0 : 'No recipes were found for the pantry'
	}

	@Then("I see the available ingredients")
	def verifyAvailableIngredients() {
		verifyIngredientSection('section_Available ingredients', 'available')
	}

	@Then("I see the missing ingredients")
	def verifyMissingIngredients() {
		verifyIngredientSection('section_Missing ingredients', 'missing')
	}

	@Then("I see the ingredient overlap percentage")
	def verifyOverlapPercentage() {
		WebUI.waitForElementVisible(object('p_Overlap percentage'), TIMEOUT)
		String percentage = WebUI.getText(object('p_Overlap percentage')).trim()
		assert percentage ==~ /\d{1,3}%/ : "Unexpected overlap percentage: '${percentage}'"
	}

	@Then("recipes using {string} are shown with higher priority")
	def verifyExpiringFirst(String ingredient) {
		List<Boolean> usesExpiring = recipeCards().collect { card ->
			String text = card.getText()
			text.contains('expiring soon') && text.contains(ingredient)
		}
		assert !usesExpiring.isEmpty() : 'No recipes were found for the pantry'
		assert usesExpiring[0] : "The first recipe does not use the expiring '${ingredient}'"
		int firstWithout = usesExpiring.indexOf(false)
		assert firstWithout == -1 || !usesExpiring.subList(firstWithout, usesExpiring.size()).contains(true) :
		"A recipe using '${ingredient}' is shown after a recipe that does not use it"
	}

	@Then("I do not see recipes containing animal products")
	def verifyNoAnimalProducts() {
		List<String> notVegan = recipeCards()
				.findAll { it.getText().contains('Contains animal products') }
				.collect { it.getAttribute('data-recipe-name') }
		assert notVegan.isEmpty() : "Recipes with animal products are shown: ${notVegan}"
	}

	// Helpers ------------------------------------------------------------------

	/** Test object from Object Repository/Page_RecipeDiscovery, with the optional itemName variable. */
	private TestObject object(String name, String itemName = null) {
		return findTestObject("Page_RecipeDiscovery/${name}", [('itemName') : itemName])
	}

	/** Builds a TestObject that targets an element by its data-testid attribute. */
	private TestObject byTestId(String testId) {
		TestObject testObject = new TestObject(testId)
		testObject.addProperty('css', ConditionType.EQUALS, "[data-testid='${testId}']")
		return testObject
	}

	/** All recipe cards in the results, in the order they are shown. */
	private List<WebElement> recipeCards() {
		return WebUI.findWebElements(byTestId('recipe-card'), TIMEOUT)
	}

	private void verifyIngredientSection(String section, String status) {
		WebUI.waitForElementVisible(object(section), TIMEOUT)
		List<WebElement> rows = WebUI.findWebElements(object(section), TIMEOUT)[0]
				.findElements(org.openqa.selenium.By.cssSelector("[data-testid='recipe-ingredient-availability-status'][data-status='${status}']"))
		assert rows.size() > 0 : "No ${status} ingredients are shown"
	}

	/** Adds an item through the "Add item" form and checks that it appears in the pantry. */
	private void addItem(String name, LocalDate expirationDate) {
		WebUI.waitForElementClickable(byTestId('add-item-button'), TIMEOUT)
		WebUI.click(byTestId('add-item-button'))
		WebUI.setText(byTestId('item-name-input'), name)
		WebUI.selectOptionByValue(byTestId('category-select'), 'Vegetables', false)
		WebUI.setText(byTestId('quantity-input'), '1')
		WebUI.selectOptionByValue(byTestId('unit-select'), 'units', false)
		WebUI.setText(byTestId('expiration-date-input'), expirationDate.format(DateTimeFormatter.ofPattern('ddMMyyyy')))
		WebUI.click(byTestId('save-item-button'))
		WebUI.waitForElementVisible(byTestId('pantry-item-' + name), TIMEOUT)
	}
}
