# Release Management

## Tools:
- [Fastlane](https://fastlane.tools/): To get started with Fastlane, you can follow the [official guide](https://docs.fastlane.tools/getting-started/android/setup/).
- [Supply](https://docs.fastlane.tools/actions/supply/): To get started with Supply, you can follow the [quick start guide](https://docs.fastlane.tools/actions/supply/#quick-start).

## Setup:

- [Install Fastlane](https://docs.fastlane.tools/getting-started/android/setup/).
- [Configure Supply](https://docs.fastlane.tools/actions/supply/).

## Publishing:

- Every time you run fastlane, use `bundle exec fastlane [lane]`.
- On your CI, add `bundle install` as your first build step.
- To update fastlane, just run `bundle update fastlane`

