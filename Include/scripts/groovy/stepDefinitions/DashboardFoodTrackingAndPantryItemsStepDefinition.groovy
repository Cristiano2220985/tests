package stepDefinitions

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Glue code for the feature "Dashboard, food tracking and pantry items".
 *
 * All elements are located through data-testid attributes (the contract between the app and the
 * tests). The generic steps "I tap the {string} button", "I insert {string} in the {string} field"
 * and "I close the AUT" are shared and defined in OnboardingProfileCreationAndSwitchingStepDefinition.
 */
class DashboardFoodTrackingAndPantryItemsStepDefinition {

	private static final String DEFAULT_PROFILE = 'Family'

	// Given: set up the context ------------------------------------------------

	@Given("I have completed the onboarding")
	def completeOnboarding() {
		new OnboardingProfileCreationAndSwitchingStepDefinition().completeOnboardingWithProfile(DEFAULT_PROFILE)
	}

	@Given("the pantry is empty")
	def verifyPantryIsEmpty() {
		WebUI.verifyElementVisible(byTestId('pantry-empty'))
	}

	@Given("the pantry contains the item {string} with the quantity {string}")
	def addItemWithQuantity(String itemName, String quantity) {
		addItem(itemName, 'Dairy', quantity, 'L', LocalDate.now().plusDays(30))
	}

	// When: perform the actions ------------------------------------------------

	@When("I select the {string} category")
	def selectCategory(String category) {
		WebUI.selectOptionByLabel(byTestId('category-select'), category, false)
	}

	@When("I select the {string} unit")
	def selectUnit(String unit) {
		WebUI.selectOptionByLabel(byTestId('unit-select'), unit, false)
	}

	@When("I set the expiration date to {string}")
	def setExpirationDate(String date) {
		setExpirationDate_1(LocalDate.parse(date, DateTimeFormatter.ofPattern('dd/MM/yyyy')))
	}

	@When("I add the item {string} expiring tomorrow")
	def addItemExpiringTomorrow(String itemName) {
		addItem(itemName, 'Vegetables', '1', 'units', LocalDate.now().plusDays(1))
	}

	@When("I tap the item {string}")
	def tapItem(String itemName) {
		WebUI.click(byTestId('pantry-item-' + itemName))
	}

	// Then: verify the expected outcomes ---------------------------------------

	@Then("I see {string} in the pantry")
	def verifyItemInPantry(String itemName) {
		WebUI.verifyElementVisible(byTestId('pantry-item-' + itemName))
	}

	@Then("I see {string} in the expiry alert tray")
	def verifyItemInAlertTray(String itemName) {
		WebUI.verifyElementVisible(byTestId('expiring-tray-item-' + itemName))
	}

	@Then("I see {string} highlighted in the pantry")
	def verifyItemHighlighted(String itemName) {
		WebUI.verifyEqual(WebUI.getAttribute(byTestId('pantry-item-' + itemName), 'data-highlighted'), 'true')
	}

	@Then("I see {string} with the quantity {string} in the pantry")
	def verifyItemQuantity(String itemName, String quantity) {
		WebUI.verifyEqual(WebUI.getText(byTestId('pantry-item-quantity-' + itemName)), quantity)
	}

	// Helpers ------------------------------------------------------------------

	/** Builds a TestObject that targets an element by its data-testid attribute. */
	private TestObject byTestId(String testId) {
		TestObject testObject = new TestObject(testId)
		testObject.addProperty('css', ConditionType.EQUALS, "[data-testid='${testId}']")
		return testObject
	}

	/** Adds an item through the "Add item" form and checks that it appears in the pantry. */
	private void addItem(String name, String category, String quantity, String unit, LocalDate expirationDate) {
		WebUI.click(byTestId('add-item-button'))
		WebUI.setText(byTestId('item-name-input'), name)
		selectCategory(category)
		WebUI.setText(byTestId('quantity-input'), quantity)
		selectUnit(unit)
		setExpirationDate(expirationDate)
		WebUI.click(byTestId('save-item-button'))
		verifyItemInPantry(name)
	}

	/**
	 * Sets the date input value (yyyy-MM-dd). Typing into a date input depends on the browser
	 * language (dd/mm/yyyy or mm/dd/yyyy), so the value is set directly and the "input" event is
	 * fired to update the form.
	 */
	private void setExpirationDate_1(LocalDate date) {
		WebUI.executeJavaScript(
				"arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input'))",
				[WebUI.findWebElement(byTestId('expiration-date-input')), date.toString()])
	}
}
