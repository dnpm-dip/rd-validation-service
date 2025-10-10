# Changelog

## [1.1.1](https://github.com/dnpm-dip/rd-validation-service/compare/v1.1.0...v1.1.1) (2025-10-10)


### Bug Fixes

* Added missing source file ([bb4fcec](https://github.com/dnpm-dip/rd-validation-service/commit/bb4fcec462646881bcca8c1f75beab0925e2ba22))

## [1.1.0](https://github.com/dnpm-dip/rd-validation-service/compare/v1.0.4...v1.1.0) (2025-10-10)


### Features

* Added implementation for (quarterly) MVH reporting ([0934b25](https://github.com/dnpm-dip/rd-validation-service/commit/0934b2543419683012905ff95fdb151f24e5c7a0))

## [1.0.4](https://github.com/dnpm-dip/rd-validation-service/compare/v1.0.3...v1.0.4) (2025-09-08)


### Bug Fixes

* Removed validation enforcing GMFCS status in follow-ups, as it is not always defined; Upgraded service-base version ([bc7706b](https://github.com/dnpm-dip/rd-validation-service/commit/bc7706b93b8ede586f65cd98f1cd9fdf1e5917ec))

## [1.0.3](https://github.com/dnpm-dip/rd-validation-service/compare/v1.0.2...v1.0.3) (2025-08-21)


### Bug Fixes

* Updated service-base version ([9a6cd22](https://github.com/dnpm-dip/rd-validation-service/commit/9a6cd2278ad899c16494fc13de95f37ad2885612))

## [1.0.2](https://github.com/dnpm-dip/rd-validation-service/compare/v1.0.1...v1.0.2) (2025-08-19)


### Bug Fixes

* Upgraded service-base dependency version ([1689f07](https://github.com/dnpm-dip/rd-validation-service/commit/1689f0725a6fda96521fe38ea4a7b5884cb178ea))

## [1.0.1](https://github.com/dnpm-dip/rd-validation-service/compare/v1.0.0...v1.0.1) (2025-08-11)


### Bug Fixes

* Updated dependency version ([e04bf22](https://github.com/dnpm-dip/rd-validation-service/commit/e04bf220650c895f87b58ce8b854e4a30820a2f5))

## 1.0.0 (2025-08-06)


### Features

* Adapted validators and tests to updated model: Upgraded to Scala 2.13.16 ([8265285](https://github.com/dnpm-dip/rd-validation-service/commit/8265285b041002954ac184cec25c216b30037f2a))
* Added further validations to RDPatientRecord; Adapted Tests to refactored base ([9d5ad59](https://github.com/dnpm-dip/rd-validation-service/commit/9d5ad593cd56908780b3c9174eccb9b504558786))
* Further work on validators; Upgrade of scalatest version ([069bf11](https://github.com/dnpm-dip/rd-validation-service/commit/069bf11fe25e9cd5c346f573f3daa51a9f203421))


### Bug Fixes

* Adapted scalac linting and fixed many reported errors (mostly unused imports) ([7a1fb3e](https://github.com/dnpm-dip/rd-validation-service/commit/7a1fb3ef548e599c39b5acbbba97018d47acff4a))
* Adapted to changed cardinality of RDPatientRecord.carePlans ([756aeda](https://github.com/dnpm-dip/rd-validation-service/commit/756aeda54a374a9772e16269aec66a53df2931b1))
