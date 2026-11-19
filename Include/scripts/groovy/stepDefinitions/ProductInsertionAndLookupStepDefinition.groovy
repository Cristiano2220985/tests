package stepDefinitions

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
class ProductInsertionAndLookupStepDefinition {

	private static final int TIMEOUT = 10
	private static final String IMAGES_FOLDER = 'Data Files/images'

	/** Form fields checked after a barcode is read. */
	private static final Map<String, String> FIELD_TEST_IDS = [
		'Item name': 'item-name-input',
		'Quantity' : 'quantity-input',
		'Unit'     : 'unit-select',
	]

	/** Messages shown by the barcode scanner. */
	private static final Map<String, String> MESSAGE_TEST_IDS = [
		'Camera access is not available': 'barcode-camera-unavailable-message',
		'Product not found'             : 'barcode-product-not-found-message',
	]

	// Given: set up the context ------------------------------------------------

	@Given("camera access is allowed")
	def allowCameraAccess() {
		WebUI.executeJavaScript('''
			const canvas = document.createElement('canvas')
			canvas.width = 640
			canvas.height = 360
			const context = canvas.getContext('2d')
			window.fakeCamera = { barcode: null }
			setInterval(() => {
				context.fillStyle = 'white'
				context.fillRect(0, 0, canvas.width, canvas.height)
				const barcode = window.fakeCamera.barcode
				if (barcode) {
					context.drawImage(barcode, (canvas.width - barcode.width) / 2, (canvas.height - barcode.height) / 2)
				}
			}, 100)
			navigator.mediaDevices.getUserMedia = () => Promise.resolve(canvas.captureStream(10))''', null)
	}

	@Given("camera access is denied")
	def denyCameraAccess() {
		WebUI.executeJavaScript('''
			navigator.mediaDevices.getUserMedia = () =>
				Promise.reject(new DOMException('Permission denied', 'NotAllowedError'))''', null)
	}

	// When: perform the actions ------------------------------------------------

	/** Shows the barcode to the fake camera (see "camera access is allowed"). */
	@When("I scan the barcode {string}")
	def scanBarcode(String barcode) {
		WebUI.waitForElementVisible(byTestId('barcode-scanner-preview'), TIMEOUT)
		String barcodeImage = imageFile("barcode-${barcode}.png").bytes.encodeBase64().toString()
		WebUI.executeJavaScript('''
			const image = new Image()
			image.onload = () => { window.fakeCamera.barcode = image }
			image.src = 'data:image/png;base64,' + arguments[0]''', [barcodeImage])
	}

	@When("I upload the image {string}")
	def uploadImage(String fileName) {
		WebUI.uploadFile(byTestId('barcode-image-file-input'), imageFile(fileName).getCanonicalPath())
	}

	// Then: check the outcome --------------------------------------------------

	@Then("I see {string} in the {string} field")
	def verifyFieldValue(String value, String fieldLabel) {
		WebUI.verifyElementAttributeValue(byTestId(FIELD_TEST_IDS[fieldLabel]), 'value', value, TIMEOUT)
	}

	@Then("I see the message {string}")
	def verifyMessage(String message) {
		TestObject messageObject = byTestId(MESSAGE_TEST_IDS[message])
		WebUI.waitForElementVisible(messageObject, TIMEOUT)
		WebUI.verifyEqual(WebUI.getText(messageObject), message)
	}

	@Then("I see the {string} button")
	def verifyFallbackButton(String buttonText) {
		String buttonTestId = toTestId(buttonText) + '-button'
		WebUI.verifyElementVisible(byCss("[data-testid='barcode-fallback-options'] [data-testid='${buttonTestId}']"))
	}

	// Helpers ------------------------------------------------------------------

	private File imageFile(String fileName) {
		return new File(RunConfiguration.getProjectDir(), "${IMAGES_FOLDER}/${fileName}")
	}

	/** Builds a TestObject that targets an element by its data-testid attribute. */
	private TestObject byTestId(String testId) {
		return byCss("[data-testid='${testId}']")
	}

	private TestObject byCss(String css) {
		TestObject testObject = new TestObject(css)
		testObject.addProperty('css', ConditionType.EQUALS, css)
		return testObject
	}

	/** Converts a visible text into the test id convention: "Upload image" -> "upload-image". */
	private String toTestId(String text) {
		return text.toLowerCase().replaceAll(/[^a-z0-9]+/, '-').replaceAll(/^-|-$/, '')
	}
}
