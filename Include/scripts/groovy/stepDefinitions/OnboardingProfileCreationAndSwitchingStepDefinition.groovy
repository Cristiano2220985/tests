package stepDefinitions

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

/**
 * Glue code for the feature "Onboarding, profile creation and switching".
 *
 * All elements are located through data-testid attributes (the contract between the app and the
 * tests), so styles and DOM structure can change without breaking the tests.
 * Buttons and fields follow a naming convention, e.g. "Get started" -> "get-started-button"
 * and "Profile name" -> "profile-name-input".
 */
class OnboardingProfileCreationAndSwitchingStepDefinition {

	private static final String APP_URL = 'http://localhost:5173/'
	private static final int TIMEOUT = 5

	// Given: set up the context ------------------------------------------------

	@Given("I start the application for the first time")
	def startApplicationForTheFirstTime() {
		WebUI.openBrowser('')
		WebUI.navigateToUrl(APP_URL)
		WebUI.executeJavaScript('window.localStorage.clear()', null)
		WebUI.refresh()
	}

	@Given("I have completed the onboarding with the profile {string}")
	def completeOnboardingWithProfile(String profileName) {
		startApplicationForTheFirstTime()
		tapButton('Get started')
		completeSetupForProfile(profileName)
	}

	@Given("I have the profiles {string} and {string}")
	def createProfiles(String firstProfile, String secondProfile) {
		completeOnboardingWithProfile(firstProfile)
		openProfileSwitcher()
		tapButton('+ Add new profile')
		completeSetupForProfile(secondProfile)
	}

	@Given("I have the profile {string} active")
	def activateProfile(String profileName) {
		openProfileSwitcher()
		chooseProfile(profileName)
		verifyActiveProfile(profileName)
	}

	// When: perform the actions ------------------------------------------------

	@When("I tap the {string} button")
	def tapButton(String buttonText) {
		click(toTestId(buttonText) + '-button')
	}

	@When("I insert {string} in the {string} field")
	def insertTextInField(String text, String fieldLabel) {
		String fieldTestId = toTestId(fieldLabel) + '-input'
		WebUI.waitForElementVisible(byTestId(fieldTestId), TIMEOUT)
		WebUI.setText(byTestId(fieldTestId), text)
	}

	@When("I set the typical servings to {int}")
	def setTypicalServings(int servings) {
		while (getServings() < servings) {
			click('servings-increase')
		}
		while (getServings() > servings) {
			click('servings-decrease')
		}
		WebUI.verifyEqual(getServings(), servings)
	}

	@When("I select the {string} dietary lifestyle")
	def selectDietaryLifestyle(String lifestyle) {
		selectOption('lifestyle-' + toTestId(lifestyle))
	}

	@When("I select the {string} allergy")
	def selectAllergy(String allergy) {
		selectOption('allergy-' + toTestId(allergy))
	}

	@When("I add {string} to the excluded ingredients")
	def addExcludedIngredient(String ingredient) {
		WebUI.waitForElementVisible(byTestId('excluded-ingredient-input'), TIMEOUT)
		WebUI.setText(byTestId('excluded-ingredient-input'), ingredient)
		click('excluded-ingredient-add')
		WebUI.verifyElementVisible(byTestId('excluded-' + ingredient))
	}

	@When("I tap the profile switcher in the header")
	def openProfileSwitcher() {
		click('profile-switcher-toggle')
		WebUI.waitForElementVisible(byTestId('profile-switcher-menu'), TIMEOUT)
	}

	@When("I complete the setup for the profile {string}")
	def completeSetupForProfile(String profileName) {
		insertTextInField(profileName, 'Profile name')
		tapButton('Next')
		tapButton('Next')
		tapButton('Finish setup')
		verifyDashboard()
	}

	@When("I choose the profile {string}")
	def chooseProfile(String profileName) {
		click('profile-option-' + profileName)
	}

	// Then: verify the expected outcomes ---------------------------------------

	@Then("I see the dashboard")
	def verifyDashboard() {
		WebUI.waitForElementVisible(byTestId('pantry-title'), TIMEOUT)
		WebUI.verifyEqual(WebUI.getText(byTestId('pantry-title')), 'My Pantry')
	}

	@Then("I see {string} as the active profile")
	def verifyActiveProfile(String profileName) {
		WebUI.waitForElementVisible(byTestId('active-profile-name'), TIMEOUT)
		WebUI.verifyEqual(WebUI.getText(byTestId('active-profile-name')), profileName)
	}

	@Then("I see {string} in the list of profiles")
	def verifyProfileInList(String profileName) {
		WebUI.verifyElementVisible(byTestId('profile-option-' + profileName))
	}

	@Then("I close the AUT")
	def closeApplication() {
		WebUI.closeBrowser()
	}

	// Helpers ------------------------------------------------------------------

	/** Builds a TestObject that targets an element by its data-testid attribute. */
	private TestObject byTestId(String testId) {
		TestObject testObject = new TestObject(testId)
		testObject.addProperty('css', ConditionType.EQUALS, "[data-testid='${testId}']")
		return testObject
	}

	/** Converts a visible text into the test id convention: "+ Add new profile" -> "add-new-profile". */
	private String toTestId(String text) {
		return text.toLowerCase().replaceAll(/[^a-z0-9]+/, '-').replaceAll(/^-|-$/, '')
	}

	private void click(String testId) {
		WebUI.waitForElementClickable(byTestId(testId), TIMEOUT)
		WebUI.click(byTestId(testId))
	}

	/** Clicks an option chip and checks that it became selected. */
	private void selectOption(String testId) {
		click(testId)
		WebUI.verifyElementAttributeValue(byTestId(testId), 'aria-pressed', 'true', TIMEOUT)
	}

	private int getServings() {
		WebUI.waitForElementVisible(byTestId('servings-value'), TIMEOUT)
		return WebUI.getText(byTestId('servings-value')).toInteger()
	}
}
