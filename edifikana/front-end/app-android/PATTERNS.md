

# UI Layer
The UI layer is implemented using Compose. The UI layer is responsible for displaying the
data to the user and handling user interactions. The UI layer is composed of the following
components:
- Screens: Represent a single screen in the app. Each screen is composed of multiple subcomponents.
  - **Screens are identified by being named \<Feature\>Screen.**
- Components: Represent a reusable UI component used to build the UI.
- Each Component or Screen will be located in a file with a single public `@Composable` function and at least
one private `@Preview` function.
- If the components or screens require to display information, this should be done through a UiModel.
  - The **UiModels are named following the pattern \<Feature\>UiModel.** Since a single screen may need multiple 
  UiModels, these can be grouped in a single file named **\<Feature\>UiModels.kt**. 
- Helper functions and classes can be defined in the same file as the component or screen if their scope is limited to 
that file.
- `@Composable` functions do no business logic and are purely declarative. Side effects are acceptable only if their use
is intrinsic to the Component/Screen.
- User interactions are handled using callbacks.
  - **Callbacks are named following the pattern on\<UIComponentIdentifier\>\<Action\>.** Examples are:
    - onShareButtonClick
    - onCardTap
    - onSearchQueryChange
  - Callbacks **must not** be called after the action they are expected to perform, since this strong coupling 
  of components and their intended use. 
- Screens are special cases for `@Composables` functions as they are the entry point for the UI layer and as such they
need to perform some basic scaffolding like setting lifecycle side effects.

## UI State Changes
- The UI layer will observe the UiState and Events emitted by the ViewModel and update the UI accordingly.
- The **UiState** represents the current state of the UI and how it should be rendered.
  - Generally, the UiState will be a single data class that encapsulates the different states that can be displayed. This
  approach is favored over using sealed classes with multiple states. The reason for that is that in practice, there is
  frequently overlap between states and using a single class allows for more flexibility. For example if we want to 
  display a loading spinner over a list of items, we can simply set the loading flag to true. 
  - The UiState should be named following the pattern \<Feature\>UiState.
- **Events** represent the actions that the UI should perform. For example, displaying a message toast.
  - Events are implemented using a sealed class named following the pattern \<Feature\>Event. Then each individual 
  event is implemented as a data class that extends the sealed class, named using a verb and description of the event.
  - Events also need to be tagged with a unique identifier to allow for triggering the same action multiple times. This 
  is done by adding a `val id: Int = Random.nextInt()` as part of the event constructor.

# Feature Business Logic
- A feature business logic for any UI is implemented using ViewModels. ViewModels are responsible for managing the UI state
and emitting events.
- **ViewModels are named following the pattern \<Feature\>ViewModel.**
- ViewModels's public interface will consist of the following:
  - **UiState**: Observable that represents the current state of the UI.
  - **Events**: Observable that represents the events emitted by the ViewModel.
  - **Public Functions**: Represents the actions that can be performed by the ViewModel. These functions will be named
  using verbs that describe the action they perform. Examples are:
    - fetchItems
    - shareImage
    - openCamera
  - The following are not allowed:
    - Public member function other than the ones described above(UiState and Events).
    - Any mutable state publicly accessible.
    - Public functions that do not conform to the specification described above.
    - Public functions that are not being used by the UI layer.

## Example

This shows the sequence of events that occur when a user taps a button to share an image.
1. The feature is called `ImageGallery`, and the screen is called `ImageGalleryScreen`.
2. The `@Composable` function ShareButton is called to render the button.
3. User taps the share button, which calls the `onClick`/`onShareButtonClick` callback.
4. The `onShareButtonClick` callback calls the `shareImage` function in the `ImageGalleryViewModel`.
5. The ViewModel emits a `ImageGalleryEvent.ShareImage` event.
6. The UI layer observes the event and starts a share intent.

# Data Layer
WIP
