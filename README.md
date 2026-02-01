# TransferGo
Android application demonstrating a currency conversion feature using TransferGo API, 
implemented with MVI architecture, Jetpack Compose, and Hilt for dependency injection.

## Tech Stack

- **Language:** Kotlin 2.3
- **UI:** Jetpack Compose
- **Dependency Injection:** Hilt
- **Testing:** JUnit, MockK
- **Architecture:** MVI (Model-View-Intent)
- **Networking:** Retrofit + Moshi
- **Coroutines:** Kotlinx.coroutines
- **Code Quality:** Ktlint (formatting & linting)
- **Build Tools:** AGP 8.9.1, JDK 21
- **Minimum SDK:** 27, Target SDK: 36

## Features


- Supported currencies: PLN, EUR, GBP, UAH
- Real-time FX rates via TransferGo API
- Limits for sending currency
- No limits for receiving currency
- Swap FROM/TO currencies with a single click
- Live updates of conversion amounts and rate
- Error handling for network issues and exceeded limits
- Default conversion: PLN → UAH, 300.00 PLN
- Fully reactive MVI architecture
- Multi-module project: app, domain, data

## Take it

1. Clone the repository:

```bash
git clone https://github.com/Blazeje/TransferGo.git
