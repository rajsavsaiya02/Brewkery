# ☕ Brewkery — Native Android Artisan Coffee & Bakery Ordering App

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2026.02.01-4285F4.svg?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Android SDK](https://img.shields.io/badge/API-29%2B%20%28Target%2037%29-3DDC84.svg?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/about/versions)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20MVVM%20%2B%20UDF-FF6F00.svg?style=for-the-badge)](https://developer.android.com/topic/architecture)
[![Build Status](https://img.shields.io/badge/Tests-100%25%20PASSING-success.svg?style=for-the-badge)](#-testing--quality-assurance)

**Brewkery** is a native Android mobile ordering application engineered for an artisan coffee house and bakery. Built completely in **Kotlin** with **Jetpack Compose (Material 3)**, it follows the **Clean MVVM Architecture** and **Unidirectional Data Flow (UDF)** patterns to deliver a high-performance, edge-to-edge 100% declarative UI experience.

---

## 📸 Screen Showcase & Navigation Flow

The application implements a 4-screen ordering lifecycle:

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│  1. Menu Screen │ ────► │ 2. Detail Screen│ ────► │  3. Cart Screen │ ────► │ 4. Order Screen │
│ (Catalog & Bar) │       │   (Customizer)  │       │   (Checkout)    │       │(Ticket & Status)│
└─────────────────┘       └─────────────────┘       └─────────────────┘       └─────────────────┘
        ▲                                                                              │
        └──────────────────────────────────────────────────────────────────────────────┘
                                  Active Order Tracking Shortcut
```

### 1. 📜 Menu Catalog Screen (`MenuScreen.kt`)
- **Dynamic Header & Store Banner**: Displays a welcoming marketing banner that dynamically morphs into an **Active Order Tracker** when an order is in progress (`PREPARING` status with estimated ETA).
- **Search & Category Filtering**: Real-time text search filtering across item names and descriptions, combined with horizontal category chips (*All, Espresso, Cold Brew, Bakery, Tea*).
- **Product Cards**: Displays item images via Coil 3, tags (*Bestseller, Bakeshop Fresh*), prices, and quick customization trigger.
- **Floating Cart Banner**: Persistent sticky bottom bar showcasing live total quantity, total price, and one-tap checkout navigation.

### 2. 🎛️ Item Customizer & Detail Screen (`DetailScreen.kt`)
- **Hero Display**: Image with badges, dietary tags, and ratings.
- **Interactive Option Selectors**:
  - **Size**: Small, Medium (+$0.50), Large (+$1.00)
  - **Milk Alternatives**: Whole Milk, Oat Milk (+$0.65), Almond Milk (+$0.65), Skim Milk
  - **Sugar/Sweetness Level**: 0% Unsweetened, 25%, 50%, 75%, 100%
- **Dynamic Real-Time Pricing**: Instantly calculates unit price adjustments and updates total amount live as customization options or quantities change.
- **Quantity Stepper**: Precise increment/decrement controls with safety bounds.
- **Cart Feedback**: Direct integration with `CartRepository` firing visual confirmation toast notifications upon item addition.

### 3. 🛒 Cart & Checkout Screen (`CartScreen.kt`)
- **Itemized Custom Entries**: Lists each unique combination of item + customization parameters independently with full option descriptions (e.g., *"Large • Oat Milk • 50% Sweetness"*).
- **In-Line Quantity Modifiers**: Modify counts or remove items directly within the list view.
- **Financial Breakdown Engine**:
  - **Subtotal**: Sum of all custom line totals.
  - **Delivery Fee**: Flat $2.50 (waived dynamically if the cart is empty).
  - **Tax**: Standard 8.0% calculation with exact rounding.
  - **Total**: Guaranteed rounded financial sum.
- **Checkout Action**: One-tap order placement that generates a unique ticket ID (`#BK-XXXXX`) and transitions the repository state.

### 4. 🎟️ Order Tracking & Ticket Screen (`OrderScreen.kt`)
- **Order Confirmation Ticket**: Displays generated order ticket reference ID (e.g., `#BK-74829`).
- **Live Progress Stepper**: Interactive visual timeline tracking status (*Order Placed ➔ Preparing ➔ Out for Delivery ➔ Delivered*).
- **Estimated Wait Time & Store Details**: Live countdown ETA ("20 - 30 minutes") and pickup store location info.
- **Quick Navigation**: Instant action button returning user back to the menu screen while maintaining active order banner tracking.

---

## 🏗️ Architecture & Technical Stack

```
com.example.brewkery/
├── data/
│   ├── model/         # Immutable data models (Store, Product, Customization, CartItem, CartState, ActiveOrder)
│   ├── network/       # Retrofit 2 + OkHttp 3 API interfaces & client setup
│   └── repository/    # CartRepository (SSOT backed by StateFlow) & MenuRepository
├── domain/
│   └── PriceCalculator# Pure domain logic for mathematical calculations, tax, fees, and rounding
├── navigation/
│   └── NavGraph.kt    # Jetpack Compose Navigation routes & arguments
├── screens/
│   ├── menu/          # Menu catalog screen, search bar, category chips, ProductCard, HomeBanner
│   ├── detail/        # Item detail screen & option customizer
│   ├── cart/          # Cart screen, item list, checkout financial summary
│   └── order/         # Order tracking & ticket confirmation screen
├── ui/
│   ├── components/    # Reusable atomic UI (BrewkeryButton, QuantityStepper, StateViews, BrewkeryToast)
│   └── theme/         # Material 3 Design Tokens (Warm Espresso Palette, Typography, Shapes)
└── util/
    └── UiState.kt     # Generic LCE (Loading / Content / Error) state wrapper
```

### Key Libraries & Stack Specs

| Layer             | Library / Tool           | Version             | Purpose                                       |
|:------------------|:-------------------------|:--------------------|:----------------------------------------------|
| **Language**      | Kotlin                   | `2.2.10`            | Coroutines, Flow, Data Classes                |
| **UI Framework**  | Jetpack Compose          | `2026.02.01` (BOM)  | Declarative UI, Material 3, Edge-to-Edge      |
| **Architecture**  | MVVM + Repository        | -                   | Unidirectional Data Flow & State Management   |
| **Asynchronous**  | Kotlin Coroutines & Flow | `1.10.1`            | Asynchronous operations & reactive StateFlow  |
| **Networking**    | Retrofit 2 + OkHttp 3    | `2.11.0` / `4.12.0` | API communication & JSON parsing              |
| **Image Loading** | Coil 3                   | `3.1.0`             | Asynchronous image caching & rendering        |
| **JSON Parser**   | Gson                     | `2.11.0`            | Serializing/Deserializing store data models   |
| **Unit Testing**  | JUnit 4 + Google Truth   | `4.13.2` / `1.4.4`  | Financial calculation & repository unit tests |

---

## ⚡ Technical Highlights & Architectural Decisions

### 1. Single Source of Truth (SSOT) Cart Architecture
- `CartRepositoryImpl` manages an in-memory `StateFlow<CartState>` and `StateFlow<ActiveOrder?>`.
- Every mutation (adding item, updating quantity, removing item, placing order) passes through pure update flows.
- Items with different customization options (e.g., *Medium Oat Milk Cappuccino* vs. *Large Whole Milk Cappuccino*) are stored as distinct cart items with unique calculated keys.

### 2. Pure Mathematical Domain Engine (`PriceCalculator.kt`)
- All pricing, tax, and fee calculations are isolated in a pure domain object `PriceCalculator`.
- Prevents double-precision floating point drift (e.g., preventing `$9.39999999999` display errors) using standardized `roundToTwoDecimals` rounding logic.
- Enforces financial invariants:
  $$\text{Subtotal} = \sum (\text{Line Item Price} \times \text{Quantity})$$
  $$\text{Delivery Fee} = \begin{cases} \$0.00 & \text{if cart is empty} \\ \$2.50 & \text{if cart has items} \end{cases}$$
  $$\text{Tax} = \text{round}(\text{Subtotal} \times 0.08)$$
  $$\text{Total} = \text{Subtotal} + \text{Delivery Fee} + \text{Tax}$$

### 3. Modern Edge-to-Edge & System Bars Compliance
- Configured with `WindowCompat.setDecorFitsSystemWindows(window, false)`.
- Uses Compose `Scaffold` and `WindowInsets` padding to ensure zero overlap with status bar or bottom gesture navigation bar across all device screen ratios.

---

## 🤖 AI Workflow & Engineering Reflection (Take-Home Report)

### 1. AI Tools Utilized
- **Antigravity IDE / Gemini 3.8 Flash**: Architecture design, Compose layout alignment, data model structure, and pure financial domain engine verification.

### 2. Prompts Sent

> **Prompt 1 (Financial Calculation Engine)**:
> *"Implement a pure unidirectional `PriceCalculator` in Kotlin that calculates item unit price from base price and customization extras, subtotal, flat $2.50 delivery fee (free if subtotal is zero), and 8.0% tax with half-up rounding precision to two decimal places."*

> **Prompt 2 (Reactive Cart SSOT)**:
> *"Create a thread-safe in-memory `CartRepository` exposing `StateFlow<CartState>`. Ensure items with different customization parameters are tracked as separate entries, decrementing quantity to zero removes the entry, and placing an order generates an `ActiveOrder` ticket while resetting the cart."*

> **Prompt 3 (Compose UI & Design System)**:
> *"Build the 4-screen flow in Jetpack Compose Material 3 matching the Brewkery visual identity: warm espresso palette, category chips, floating cart bar, and an active order banner on the home screen that tracks placed orders."*

#### Note: I always preferred to use refined prompt of give my raw context so it delivered be more accurate and expected outcome.

### 3. What AI Excelled At
- **Declarative UI**: Rapidly scaffolded Material 3 Compose screens with smooth layout hierarchy and proper state hoisting.
- **Data Modeling**: Accurately generated Kotlin data classes (`ProductDto`, `OptionCategoryDto`) matching nested JSON structures.
- **Edge-to-Edge Compatibility**: Handled window inset handling out of the box.

### 4. Edge Cases Caught & Resolved
- **The Bug**: Initial AI generated logic applied the flat $2.50 delivery fee unconditionally—even when the cart was empty—causing an empty cart screen to present a $2.50 total payable charge. Furthermore, double floating point math caused precision drift (e.g., `$9.3999999`).
- **The Fix**: Introduced a pure domain guard clause `if (items.isEmpty()) return CartState()` inside `PriceCalculator.kt` and standardized financial rounding via `roundToTwoDecimals()`. Created comprehensive unit tests (`CartCalculationTest.kt`) to guarantee financial accuracy across empty, single, and multi-item cart states.

---

## 🧪 Testing & Quality Assurance

Unit tests are written using **JUnit 4** and **Google Truth** assertions to verify financial math and cart repository state transitions.

### Running Unit Tests

To run unit tests from terminal:
```bash
./gradlew testDebugUnitTest
```

### Verified Test Suites

1. `CartCalculationTest.kt`:
   - `empty cart produces zeroed CartState` (Verifies subtotal, delivery fee, tax, total are all $0.00).
   - `single customized item calculates correct totals` (Verifies base price + customization extra + delivery fee + 8% tax).
   - `multi item multi quantity calculates correctly` (Verifies multi-quantity line totals and grand totals).
   - `currency formatter formats double correctly` (Verifies `$X.XX` formatting).

2. `CartRepositoryTest.kt`:
   - `addToCart adds new item to cartState`
   - `addToCart merges duplicate item by incrementing quantity`
   - `updateQuantity removes item when quantity reaches zero`
   - `placeOrder generates active order and clears cart`

```
BUILD SUCCESSFUL in 1.2s
8 actionable tasks: 8 executed
All unit tests PASSED.
```

---

## 🚀 Getting Started & Build Instructions

### Prerequisites
- **JDK**: Java 17 or Java 21
- **Android SDK**: API 35 / API 37 (Minimum SDK: 29)
- **Android Studio**: Android Studio Ladybug / Meerkat (2024.2+) or later

### Building & Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/Brewkery.git
   cd Brewkery
   ```

2. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

3. **Install on Connected Device / Emulator**:
   ```bash
   ./gradlew installDebug
   ```

### Pre-Built Executable APK
An executable debug APK is available directly in the project repository at:
`apks/brewkery-debug.apk`

---

## 📄 License & Credits

Developed as part of the ClickRetina Senior Android Engineer take-home assessment.
Designed & Built with ❤️ in Kotlin and Jetpack Compose.
