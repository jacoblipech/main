# taojiashu
###### /java/seedu/address/model/person/Favourite.java
``` java
/**
 *  Represents whether a Person is a favourite contact or not
 */
public class Favourite {

    private boolean favourite;
    private String status;

    /**
     * Default constructor
     * if no parameter is passed in, the favourite value is initialised to false
     */
    public Favourite() {
        this.favourite = false;
        this.status = "False";
    }

    public Favourite(boolean favourite) {
        this.favourite = favourite;
        this.status = favourite ? "True" : "False";
    }

    private void updateFavouriteStatus() {
        status = favourite ? "True" : "False";
    }

    /**
     * Sets favourite to the opposite value.
     * Updates the status too.
     */
    public void toggleFavourite() {
        favourite = !favourite;
        updateFavouriteStatus();
    }

    public boolean isFavourite() {
        return favourite;
    }

    public String getStatus() {
        updateFavouriteStatus(); // Ensure the status is in sync with favourite
        return status;
    }

    @Override
    public String toString() {
        updateFavouriteStatus(); // Ensure the status is in sync with favourite
        return status;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Favourite) // instanceof handles nulls
                && this.favourite == ((Favourite) other).favourite; // state check
    }

    @Override
    public int hashCode() {
        updateFavouriteStatus();
        return status.hashCode();
    }
}
```
###### /java/seedu/address/model/person/IsFavouritePredicate.java
``` java
/**
 * Tests that a {@code ReadOnlyPerson}'s {@code Favourite} is "True".
 */
public class IsFavouritePredicate implements Predicate<ReadOnlyPerson> {

    @Override
    public boolean test(ReadOnlyPerson person) {
        return person.getFavourite().isFavourite();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof IsFavouritePredicate); // instanceof handles nulls
    }
}
```
###### /java/seedu/address/logic/commands/ShowFavouriteCommand.java
``` java
/**
 * Lists all favourite persons in UniCity to the user upon execution.
 */
public class ShowFavouriteCommand extends Command {

    public static final String COMMAND_WORD_1 = "showFavourite";
    public static final String COMMAND_WORD_2 = "sf";

    public static final String MESSAGE_USAGE = COMMAND_WORD_1
            + " OR "
            + COMMAND_WORD_2;

    @Override
    public CommandResult execute() {
        final IsFavouritePredicate predicate = new IsFavouritePredicate();
        model.updateFilteredPersonList(predicate);
        return new CommandResult((getMessageForPersonListShownSummary(model.getFilteredPersonList().size())));
    }
}
```
###### /java/seedu/address/logic/commands/FavouriteCommand.java
``` java
/**
 * Marks a person with the given index number in UniCity as favourite.
 */
public class FavouriteCommand extends Command {

    public static final String COMMAND_WORD_1 = "favourite";
    public static final String COMMAND_WORD_2 = "fav";

    public static final String MESSAGE_USAGE = COMMAND_WORD_1 + ": Mark the person as favourite "
            + "by the index number used in the last person listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD_1 + " 1"
            + " OR "
            + COMMAND_WORD_2 + " 1 ";

    public static final String MESSAGE_ARGUMENTS = "INDEX: %1$d";
    public static final String MESSAGE_FAVOURITE_SUCCESS = "Favourite Person: %1$s";
    public static final String MESSAGE_DEFAVOURITE_SUCCESS = "Remove Person from Favourites: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Index index;
    private boolean changedToFav;

    /**
     * @param index of the person in the filtered person list to mark as favourite
     */
    public FavouriteCommand(Index index) {
        requireNonNull(index);

        this.index = index;
    }

    @Override
    public CommandResult execute() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson personToEdit = lastShownList.get(index.getZeroBased());
        Favourite favourite = personToEdit.getFavourite();
        favourite.toggleFavourite();
        changedToFav = favourite.isFavourite();

        Person editedPerson = new Person(personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), favourite, personToEdit.getBirthday(), personToEdit.getTags());

        try {
            model.updatePerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        if (changedToFav) {
            return new CommandResult(generateFavouriteSuccessMessage(editedPerson));
        } else {
            return new CommandResult(generateDeFavouriteSuccessMessage(editedPerson));
        }
    }

    private String generateFavouriteSuccessMessage(ReadOnlyPerson personToEdit) {
        return String.format(MESSAGE_FAVOURITE_SUCCESS, personToEdit);
    }

    private String generateDeFavouriteSuccessMessage(ReadOnlyPerson personToEdit) {
        return String.format(MESSAGE_DEFAVOURITE_SUCCESS, personToEdit);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if they are the same object
        if (other == this) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof FavouriteCommand)) {
            return false;
        }

        // state check
        FavouriteCommand e = (FavouriteCommand) other;
        return index.equals(e.index);
    }

}
```
###### /java/seedu/address/logic/commands/ExitCommand.java
``` java
    public static final String MESSAGE_CONFIRMATION = "Type 'exit' again to confirm to exit";
    public static final String MESSAGE_EXIT_ACKNOWLEDGEMENT = "Exiting Address Book as requested ...";

```
###### /java/seedu/address/logic/commands/ExitCommand.java
``` java
    @Override
    public CommandResult execute() {
        List<String> previousCommands = history.getHistory();

        if (previousCommands.isEmpty()) {
            return new CommandResult(MESSAGE_CONFIRMATION);
        }

        Collections.reverse(previousCommands);
        if (previousCommands.get(0).equals("exit")) {
            EventsCenter.getInstance().post(new ExitAppRequestEvent());
            return new CommandResult(MESSAGE_EXIT_ACKNOWLEDGEMENT);
        } else {
            return new CommandResult(MESSAGE_CONFIRMATION);
        }
    }

```
###### /java/seedu/address/logic/commands/ExitCommand.java
``` java
    @Override
    public void setData(Model model, CommandHistory history, UndoRedoStack undoRedoStack) {
        requireNonNull(history);
        this.history = history;
    }
```
###### /java/seedu/address/logic/commands/ExitCommand.java
``` java
}
```
###### /java/seedu/address/logic/commands/LocateCommand.java
``` java
/**
 * Displays the location of the person with the given index in the contact list in Google Maps.
 */
public class LocateCommand extends Command {

    public static final String COMMAND_WORDVAR = "locate";

    public static final String MESSAGE_USAGE = COMMAND_WORDVAR
            + ": Displays the location of the person identified by the index number in the latest person listing."
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORDVAR + " 1";

    public static final String MESSAGE_LOCATE_PERSON_SUCCESS = "Address of the person is displayed";
    public static final String MESSAGE_NO_ADDRESS = "Address of this person has not been inputted.";

    private final Index targetIndex;

    public LocateCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute() throws CommandException {

        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson person = lastShownList.get(targetIndex.getZeroBased());
        String address = person.getAddress().toString();

        if (address.equals("No Address Added")) {
            return new CommandResult(String.format(MESSAGE_NO_ADDRESS, targetIndex.getOneBased()));
        }

        EventsCenter.getInstance().post(new ShowLocationRequestEvent(address));
        return new CommandResult(String.format(MESSAGE_LOCATE_PERSON_SUCCESS, targetIndex.getOneBased()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof LocateCommand // instanceof handles nulls
                && this.targetIndex.equals(((LocateCommand) other).targetIndex)); // state check
    }
}
```
###### /java/seedu/address/logic/parser/AddressBookParser.java
``` java
        } else if (commandWord.equalsIgnoreCase(FavouriteCommand.COMMAND_WORD_1)
                || commandWord.equalsIgnoreCase(FavouriteCommand.COMMAND_WORD_2)) {
            return new FavouriteCommandParser().parse(arguments);

        } else if (commandWord.equalsIgnoreCase(ShowFavouriteCommand.COMMAND_WORD_1)
                || commandWord.equalsIgnoreCase(ShowFavouriteCommand.COMMAND_WORD_2)) {
            return new ShowFavouriteCommand();
```
###### /java/seedu/address/logic/parser/AddressBookParser.java
``` java
        } else if (commandWord.equalsIgnoreCase(AddBirthdayCommand.COMMAND_WORDVAR_1)
                || commandWord.equalsIgnoreCase(AddBirthdayCommand.COMMAND_WORDVAR_2)) {
            return new AddBirthdayCommandParser().parse(arguments);

        } else if (commandWord.equalsIgnoreCase(SortCommand.COMMAND_WORDVAR_1)
                || commandWord.equalsIgnoreCase(SortCommand.COMMAND_WORDVAR_2)) {
            return new SortCommand();

        } else if (commandWord.equalsIgnoreCase(ChangeWindowSizeCommand.COMMAND_WORD)) {
            return new ChangeWindowSizeCommandParser().parse(arguments);

        } else {
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }
```
###### /java/seedu/address/logic/parser/FavouriteCommandParser.java
``` java
/**
 * Parser for FavouriteCommand
 */
public class FavouriteCommandParser implements Parser<FavouriteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FavouriteCommand
     * and returns a FavouriteCommand object for execution
     * @throws ParseException if the user input does not conform the expected format
     */
    public FavouriteCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new FavouriteCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FavouriteCommand.MESSAGE_USAGE));
        }
    }
}
```
###### /java/seedu/address/logic/parser/LocateCommandParser.java
``` java
/**
 * Parses input arguments and creates a new LocateCommand object.
 */
public class LocateCommandParser implements Parser<LocateCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the LocateCommand
     * and returns a LocateCommand object for execution
     * @throws ParseException if the user input does not conform the expected format
     */
    public LocateCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new LocateCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, LocateCommand.MESSAGE_USAGE));
        }
    }
}
```
###### /java/seedu/address/logic/parser/FacebookCommandParser.java
``` java
/**
 * Parses input arguments and creates a new FacebookCommand object.
 */
public class FacebookCommandParser implements Parser<FacebookCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FacebookCommand
     * and returns a FacebookCommand object for execution
     * @throws ParseException if the user input does not conform the expected format
     */
    public FacebookCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new FacebookCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FacebookCommand.MESSAGE_USAGE));
        }
    }
}
```
###### /java/seedu/address/storage/XmlAdaptedPerson.java
``` java
    @XmlElement(required = true)
    private String favourite;
```
###### /java/seedu/address/storage/XmlAdaptedPerson.java
``` java
        favourite = source.getFavourite().getStatus();
```
###### /java/seedu/address/storage/XmlAdaptedPerson.java
``` java
        final Favourite favourite = new Favourite();
        if (this.favourite.equals("True")) {
            favourite.toggleFavourite();
        } else if (!this.favourite.equals("False")) {
            throw new IllegalValueException("Illegal favourite status");
        }
```
###### /java/seedu/address/commons/events/ui/ShowFacebookRequestEvent.java
``` java
/**
 * Indicates a request to search a name in Facebook.
 */
public class ShowFacebookRequestEvent extends BaseEvent {

    public final String name;

    public ShowFacebookRequestEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
```
###### /java/seedu/address/commons/events/ui/ShowLocationRequestEvent.java
``` java
/**
 * Indicates a request to display the address of a person in Google Maps
 */
public class ShowLocationRequestEvent extends BaseEvent {

    public final String address;

    public ShowLocationRequestEvent(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
```
###### /java/seedu/address/ui/PersonCard.java
``` java
        initFavouriteLabel(person);
```
###### /java/seedu/address/ui/PersonCard.java
``` java
        person.favouriteProperty().addListener((observable, oldValue, newValue) -> initFavouriteLabel(person));
```
###### /java/seedu/address/ui/PersonCard.java
``` java
    /**
     * Sets the colour of a favourite label based on its favourite status
     */
    private void initFavouriteLabel(ReadOnlyPerson person) {
        boolean favouriteStatus = person.getFavourite().isFavourite();
        Label favouriteLabel = new Label();
        Image starFilled = new Image(getClass().getResource("/images/Gold_Star.png").toExternalForm());
        Image starTransparent = new Image(getClass().getResource("/images/Star_star.png").toExternalForm());
        if (favouriteStatus) {
            favouriteLabel.setGraphic(new ImageView(starFilled));
            favouriteLabel.setStyle("-fx-background-color: transparent; -fx-border-color: transparent");
        } else {
            favouriteLabel.setGraphic(new ImageView((starTransparent)));
            favouriteLabel.setStyle("-fx-background-color: transparent; -fx-border-color: transparent");

        }
        cardPane.getChildren().add(favouriteLabel);
    }
```
###### /java/seedu/address/ui/BrowserPanel.java
``` java
    public static final String FACEBOOK_SEARCH_URL = "https://m.facebook.com/search/people/?q=";

    public static final String MAPS_SEARCH_URL = "https://www.google.com.sg/maps/search/";
```
###### /java/seedu/address/ui/BrowserPanel.java
``` java
    public void loadLocationPage(String address) {
        loadPage(MAPS_SEARCH_URL + address);
    }

    public void loadFacebookPage(String name) {
        loadPage(FACEBOOK_SEARCH_URL + name);
    }
```
###### /java/seedu/address/ui/BrowserPanel.java
``` java
    @Subscribe
    private void handleLocationRequest(ShowLocationRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadLocationPage(event.getAddress());
    }

    @Subscribe
    private void handleFacebookRequest(ShowFacebookRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadFacebookPage(event.getName());
    }
```
