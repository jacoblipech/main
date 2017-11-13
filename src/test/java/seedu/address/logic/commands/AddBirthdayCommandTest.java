package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_BIRTHDAY_AMY;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Birthday;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.testutil.PersonBuilder;

//@@author jacoblipech
/**
 * Contains integration tests (interaction with the Model) and unit tests for AddBirthdayCommand.
 */

public class AddBirthdayCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addBirthday_success() throws PersonNotFoundException, IllegalValueException {
        //actual model
        Person editedPerson = new PersonBuilder(model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()))
                .withBirthday(VALID_BIRTHDAY_AMY).build();
        Birthday toAdd = new Birthday (VALID_BIRTHDAY_AMY);

        AddBirthdayCommand addBirthdayCommand = prepareCommand(INDEX_FIRST_PERSON, toAdd);
        String expectedMessage = String.format(AddBirthdayCommand.MESSAGE_ADD_BIRTHDAY_SUCCESS, toAdd);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.updatePerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(addBirthdayCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests failure of an unfiltered persons list with invalid input indexes but a valid birthday
     */
    @Test
    public void execute_invalidIndexUnfilteredList_failure() throws Exception {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        ArrayList<Index> indexes = new ArrayList<Index>();
        indexes.add(outOfBoundIndex);
        Birthday toAdd = new Birthday("05/10/1994");
        AddBirthdayCommand addBirthdayCommand = prepareCommand(indexes.get(0), toAdd);

        assertCommandFailure(addBirthdayCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() throws Exception {
        ArrayList<Index> indexes1 = new ArrayList<Index>();
        ArrayList<Index> indexes2 = new ArrayList<Index>();
        indexes1.add(INDEX_FIRST_PERSON);
        indexes1.add(INDEX_SECOND_PERSON);
        indexes2.add(INDEX_SECOND_PERSON);
        Birthday firstBirthday = new Birthday("05/10/1994");
        Birthday secondBirthday = new Birthday("21/09/98");
        final AddBirthdayCommand standardCommand = new AddBirthdayCommand(indexes1.get(0), firstBirthday);

        // same values -> returns true
        AddBirthdayCommand commandWithSameValues = new AddBirthdayCommand(indexes1.get(0), firstBirthday);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different target indexes -> returns false
        assertFalse(standardCommand.equals(new AddBirthdayCommand(indexes2.get(0), firstBirthday)));

        // different target tag -> returns false
        assertFalse(standardCommand.equals(new AddBirthdayCommand(indexes1.get(1), firstBirthday)));
    }

    /**
     * Returns an {@code AddBirthdayCommand} with parameter {@code index}
     */
    private AddBirthdayCommand prepareCommand (Index index, Birthday toAdd) {
        AddBirthdayCommand addBirthdayCommand = new AddBirthdayCommand(index, toAdd);
        addBirthdayCommand.setData(model, new CommandHistory(), new UndoRedoStack());

        return addBirthdayCommand;
    }
}
