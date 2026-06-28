import XCTest
import SwiftUI
@testable import iosApp

final class iosAppTests: XCTestCase {
    func testAppLaunchesAndLogs() throws {
        // Создаем и жестко инициализируем наше SwiftUI-окно с Compose
        let contentView = ContentView()

        // Рендерим вью в памяти симулятора
        let hostingController = UIHostingController(rootView: contentView)
        XCTAssertNotNil(hostingController.view)

        // Даем симулятору 3 секунды, чтобы отработал LaunchedEffect в Kotlin
        Thread.sleep(forTimeInterval: 3.0)
    }
}