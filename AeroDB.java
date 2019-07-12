import java.util.*;
import java.io.*;
public class AeroDB{
	static int snapshotID = 1;
	static Entry database = new Entry();
	static Snapshot snapshots = new Snapshot();
	private static final String HELP =
		"BYE   clear database and exit\n"+
		"HELP  display this help message\n"+
		"\n"+
		"LIST KEYS       displays all keys in current state\n"+
		"LIST ENTRIES    displays all entries in current state\n"+
		"LIST SNAPSHOTS  displays all snapshots in the database\n"+
		"\n"+
		"GET <key>    displays entry values\n"+
		"DEL <key>    deletes entry from current state\n"+
		"PURGE <key>  deletes entry from current state and snapshots\n"+
		"\n"+
		"SET <key> <value ...>     sets entry values\n"+
		"PUSH <key> <value ...>    pushes values to the front\n"+
		"APPEND <key> <value ...>  appends values to the back\n"+
		"\n"+
		"PICK <key> <index>   displays value at index\n"+
		"PLUCK <key> <index>  displays and removes value at index\n"+
		"POP <key>            displays and removes the front value\n"+
		"\n"+
		"DROP <id>      deletes snapshot\n"+
		"ROLLBACK <id>  restores to snapshot and deletes newer snapshots\n"+
		"CHECKOUT <id>  replaces current state with a copy of snapshot\n"+
		"SNAPSHOT       saves the current state as a snapshot\n"+
		"\n"+
		"MIN <key>  displays minimum value\n"+
		"MAX <key>  displays maximum value\n"+
		"SUM <key>  displays sum of values\n"+
		"LEN <key>  displays number of values\n"+
		"\n"+
		"REV <key>   reverses order of values\n"+
		"UNIQ <key>  removes repeated adjacent values\n"+
		"SORT <key>  sorts values in ascending order\n"+
		"\n"+
		"DIFF <key> <key ...>   displays set difference of values in keys\n"+
		"INTER <key> <key ...>  displays set intersection of values in keys\n"+
		"UNION <key> <key ...>  displays set union of values in keys\n"+
		"CARTPROD <key> <key ...>  displays set union of values in keys\n";
	
	public static void bye() {
		System.out.println("bye");
	}
	
	public static void help() {
		System.out.println(HELP);
	}
	
	public static void set(String[] inputDataSeparated){
		/*This method is required to:
		- Check if the key is already assigned to an entry
		  - If yes, replace the values with the new values
		- If no, insert the new entry with the key
		*/
		// Fetching the number of values to be inserted into the new item.
		int lenInputInts = (inputDataSeparated.length - 2);
		// Creating the array to be passed into the insertEntry method.
		int[] tempArray = new int[lenInputInts];
		// For loop to populate the array.
		for (int i = 0; i < lenInputInts; i++){
			tempArray[i] = Integer.parseInt(inputDataSeparated[2+i]);
		}
		String setKey = inputDataSeparated[1];
		// Check1, see if the database is empty and if so insert the head element.
		if (database.locateEntry(setKey) == null){
			database.insertEntry(setKey,tempArray);
			//System.out.println("Check1");
		// Check2, see if there exists an entry with key being inserted, and if so replace the pre-existing values with new values.
		}else if ((database.locateEntry(setKey).key).equals(setKey)){
			database.locateEntry(setKey).values = tempArray;
			//System.out.println("Check2");
		// Check3, insert the entry and its data. This is the final test case.
		}else{
			database.insertEntry(setKey,tempArray);
			//System.out.println("Check3");
		}
		System.out.println("ok\n");
	}
	
	public static void del(String[] inputDataSeparated){
		/* This method is sent a key to delete, and will:
			- Search the database for the entry
			- Delete the entry from the database
		*/
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if (database.locateEntry(inputDataSeparated[1]) == null){
			System.out.println("no such key\n");
			return;
		}
		// Invoking the locateEntry and deleteEntry methods previously set up in the entry file. Very satisfyingly easy...
		database.deleteEntry(database.locateEntry(inputDataSeparated[1]));
		System.out.println("ok\n");
	}
	
	public static void get(String[] inputDataSeparated){
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if (database.locateEntry(inputDataSeparated[1]) == null){
			System.out.println("no such key\n");
			return;
		}
		// Locate the entry and print its stored values.
		Entry tempEntry = new Entry();
		tempEntry = database.locateEntry(inputDataSeparated[1]);
		System.out.println((Arrays.toString(tempEntry.values)).replaceAll(",",""));
		System.out.print("\n");
	}
	
	public static void push(String[] inputDataSeparated){
		/*This method is required to:
		- Locate the entry that the new values are being pushed to.
		- Sort the values to be pushed in the correct order of addition.
		- Add these values to a temp array, in addition to any previous existing values.
		- Set the entrys values to the new temp array.
		*/
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if (database.locateEntry(inputDataSeparated[1]) == null){
			System.out.println("no such key\n");
			return;
		}
		// Taking the total number of values that will need to be in the new array.
		// First part represents the input values, second part is the pre-existing array values.
		int valuesLength = (inputDataSeparated.length - 2) + (database.locateEntry(inputDataSeparated[1]).values).length;
		// Creating the temporary array for values to be stored.
		int[] tempArray = new int[valuesLength];
		// Populating the temporary array with the data from input in the new order.
		for (int i = 0; i < (inputDataSeparated.length - 2); i++){
			tempArray[i] = Integer.parseInt(inputDataSeparated[(inputDataSeparated.length-1) - i]);
		}
		// Merging the data from the previous array to the new array.
		int j = 0;
		for (int i = (inputDataSeparated.length - 2); i < valuesLength; i++){
			tempArray[i] = (database.locateEntry(inputDataSeparated[1])).values[j];
			j++;
		}
		// Assigning the new array of values to the entry.
		database.locateEntry(inputDataSeparated[1]).values = tempArray;
		System.out.println("ok\n");
	}
	
	public static void append(String[] inputDataSeparated){
		/*This method is required to:
		- Locate the entry that the new values are being pushed to.
		- Size the original array of values, and then size the number of new values to be added.
		- Create a temp entry of size original + new, and populate with values.
		- Set previous value array to new value array.
		*/
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if (database.locateEntry(inputDataSeparated[1]) == null){
			System.out.println("no such key\n");
			return;
		}
		// First part represents the input values, second part is the pre-existing array values.
		int valuesLength = (inputDataSeparated.length - 2) + (database.locateEntry(inputDataSeparated[1]).values).length;
		// Creating the temporary array for values to be stored.
		int[] tempArray = new int[valuesLength];
		// Populating the array with the old values.
		for (int i = 0; i < (database.locateEntry(inputDataSeparated[1]).values).length; i++){
			tempArray[i] = database.locateEntry(inputDataSeparated[1]).values[i];
		}
		// Populating the remainder of the array with the new values entered via input.
		for (int i = 0; i < (inputDataSeparated.length - 2); i++){
			tempArray[i+((database.locateEntry(inputDataSeparated[1]).values).length)] = Integer.parseInt(inputDataSeparated[2 + i]);
		}
		// Set the entries values to the newly created tempArray.
		database.locateEntry(inputDataSeparated[1]).values = tempArray;
		System.out.println("ok\n");
	}
	
	public static void pick(String[] inputDataSeparated){
		/*This method is required to:
			- Locate the entry that the value is being picked from.
			- Print the value at the index specified.
		*/
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if (database.locateEntry(inputDataSeparated[1]) == null){
			System.out.println("no such key\n");
			return;
		}
		// Check to see if the index specified is correct. If not, print helpful message and exit.
		if (((database.locateEntry(inputDataSeparated[1]).values).length < (Integer.parseInt(inputDataSeparated[2]))) || (Integer.parseInt(inputDataSeparated[2]) == 0)){
			System.out.println("index out of range\n");
			return;
		}
		// Finally, locate the entry in the database, and print the Int[] values value at the index specified (the actual index is the index specified -1).
		System.out.println(database.locateEntry(inputDataSeparated[1]).values[Integer.parseInt(inputDataSeparated[2])-1]);
		System.out.print("\n");	
	}
	
	public static void pluck(String[] inputDataSeparated){
		/*This method is required to:
			- Execute pick on the specified key.
			- Remove the value at the index specified in the entry.
		*/
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if (database.locateEntry(inputDataSeparated[1]) == null){
			System.out.println("no such key\n");
			return;
		}
		// Creating new temporary entry data.
		Entry data = new Entry();
		// Assigning data to the searched entry.
		data = database.locateEntry(inputDataSeparated[1]);
		int valueLen = (data.values).length;
		// Check to see if the index specified is correct. If not, print helpful message and exit.
		if ((valueLen < Integer.parseInt(inputDataSeparated[2])) || (Integer.parseInt(inputDataSeparated[2]) == 0)){
			System.out.println("index out of range\n");
			return;
		}
		// Pick the entry to print the value at specified index.
		pick(inputDataSeparated);
		// Creating a temporary array to which the new elements will be copied into.
		int[] tempArray = new int[valueLen - 1];
		// Creating a second counter for our for loop function.
		// While loop function that scans each of the elements of the original value array, and removes the target value.
		int i = 0;
		int j = 0;
		while (i<valueLen){
			if (i != (Integer.parseInt(inputDataSeparated[2]) - 1)){
				tempArray[j] = data.values[i];
				i++;
				j++;
			}else{
				i++;
			}
		}
		// Saving the new tempArray into the entry.
		database.locateEntry(inputDataSeparated[1]).values = tempArray;
	}
	
	public static void pop(String[] inputDataSeparated){
		/*This method is required to:
			- Pluck the first value in the entrys value array.
		*/
		// Fetching the length of the values in the entry (how many values there are);
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if (database.locateEntry(inputDataSeparated[1]) == null){
			System.out.println("no such key\n");
			return;
		}
		// If there are no values in the array print nil.
		if ((database.locateEntry(inputDataSeparated[1]).values).length == 0){
			System.out.println("nil\n");
			return;
		}
		// Creating a temporary array to be passed to pluck that is of length 3;
		String[] tempArray = new String[3];
		// Populating the new temporary array with our new data.
		tempArray[0] = inputDataSeparated[0];
		tempArray[1] = inputDataSeparated[1];
		// This is the index of the element to be plucked.
		tempArray[2] = "1";
		// Calling the pluck method with the new data.
		pluck(tempArray);
	}
	
	public static void minmax(String[] inputDataSeparated, int flag){
		/* This method is required to:
		   - Loop through the values array to determine the smallest size element or the largest element based on the flag provided. 0 = min, 1 = max.
		*/
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if (database.locateEntry(inputDataSeparated[1]) == null){
			System.out.println("no such key\n");
			return;
		}
		// If there are no values in the array print nil.
		if ((database.locateEntry(inputDataSeparated[1]).values).length == 0){
			System.out.println("nil\n");
			return;
		}
		// Creating an integer to store our minimum or maximum value, and set its starting value to the first element in tempArray.
		int minmaxValue = database.locateEntry(inputDataSeparated[1]).values[0];
		// For loop to determine the minimum or maximum value in our tempArray.
		// We are looping through the int[] array within our entry object.
		for (int i = 0; i < (database.locateEntry(inputDataSeparated[1]).values).length; i++){
			// if min flag
			if ((database.locateEntry(inputDataSeparated[1]).values[i] < minmaxValue) && flag == 0){
				minmaxValue = database.locateEntry(inputDataSeparated[1]).values[i];
			}
			// if max flag
			else if((database.locateEntry(inputDataSeparated[1]).values[i] > minmaxValue) && flag == 1){
				minmaxValue = database.locateEntry(inputDataSeparated[1]).values[i];
			}
		}
		System.out.println(minmaxValue + "\n");
	}
	
	public static void len(String[] inputDataSeparated){
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if (database.locateEntry(inputDataSeparated[1]) == null){
			System.out.println("no such key\n");
			return;
		}
		// Locate the entry and print its length.
		System.out.println((database.locateEntry(inputDataSeparated[1]).values).length + "\n");
	}
	
	public static void sum(String[] inputDataSeparated){
		/*This method is required to:
		  - Loop through the entries values, and add each value at index i to te total sum of the array.
		*/
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if (database.locateEntry(inputDataSeparated[1]) == null){
			System.out.println("no such key\n");
			return;
		}
		// Create our summed values int.
		int summedValues = 0;
		// Loop through the array adding each element to summedvalues.
		for (int i = 0; i < (database.locateEntry(inputDataSeparated[1]).values).length; i++){
			summedValues = summedValues + database.locateEntry(inputDataSeparated[1]).values[i];
		}
		System.out.println(summedValues + "\n");
	}
	
	public static void rev(String[] inputDataSeparated){
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if (database.locateEntry(inputDataSeparated[1]) == null){
			System.out.println("no such key\n");
			return;
		}
		// Creating a temporary entry and assigning it to the entry corresponding with provided key.
		Entry tempEntry = new Entry();
		tempEntry = database.locateEntry(inputDataSeparated[1]);
		// Creating the new integer array to store values in reverse order.
		int[] valueArray = new int[(tempEntry.values).length];
		int j = (tempEntry.values).length - 1;
		// Looping through each index of our value array and assigning in reverse order (using j being max at start and i being min).
		for (int i = 0; i < (tempEntry.values).length; i++){
			valueArray[i] = tempEntry.values[j];
			j--;
		}
		// Assigning the new valueArray as our entries value data.
		tempEntry.values = valueArray;
		System.out.println("ok\n");
	}
	
	public static void uniq(String[] inputDataSeparated){
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if (database.locateEntry(inputDataSeparated[1]) == null){
			System.out.println("no such key\n");
			return;
		}
		// Create a temporary entry to hold the values in.
		Entry tempEntry = new Entry();
		// Assign temporary entry to our target entry to be edited.
		tempEntry = database.locateEntry(inputDataSeparated[1]);
		// Check if there are zero or 1 elements in the values array. If yes to either of these cases, simply return. 
		// Values dont need to be edited
		if ((tempEntry.values).length == 0 || (tempEntry.values).length == 1){
			return;
		}
		// Create a new temporary value array that is the same length as the unedited array supplied from entry.
		// This ensures we will have at least enough space to store the values.
		// In the case that there are multiple zeros, these should be BEFORE all other values
		// Therefore we know any 0's after a larger value are just empty positions in the array.
		int[] tempArray = new int[(tempEntry.values).length];
		int[] tempEntryArray = tempEntry.values;
		// Set to one as we dont scan last element, it is assumed that there will be a unique element.
		int uniqueElementCount = 1;
		// For loop to compare each value of the value array in the entry to every other value in the array.
		// Add each unique element to a unique element counter.
		int j = 0;
		for (int i = 0; i < tempEntryArray.length - 1; i++){
			if (tempEntryArray[i] != tempEntryArray[i+1]) {
                tempArray[j] = tempEntryArray[i]; 
				j++;
				uniqueElementCount++;
			}
		}
		// Assigning the value for the last element of the array, as this element has not been scanned yet. 
		// Therefore, it doesn't matter if it is repeated or not.
		tempArray[j] = tempEntryArray[tempEntryArray.length -1];
		// Create a re-sized Array of the appropriate size so it can be populated and passed back into the entry.
		int[] finalArray = new int[uniqueElementCount];
		for (int i = 0; i < finalArray.length; i++){
			finalArray[i] = tempArray[i];
		}
		// Passing the new array back into the entry.
		tempEntry.values = finalArray;
		System.out.println("ok\n");
	}
	
	public static void sort(String[] inputDataSeparated){
		// For our sort algorithm we will use the in-built sorting method in util.arrays.
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if (database.locateEntry(inputDataSeparated[1]) == null){
			System.out.println("no such key\n");
			return;
		}
		// Creating temp entry to access values from and re-write the sorted values to.
		Entry tempEntry = new Entry();
		tempEntry = database.locateEntry(inputDataSeparated[1]);
		// Creating a temporary array to write the sorted values into. Then sorting the values and inserting.
		int[] tempValues = new int[(tempEntry.values).length];
		// Assigning temp values to our previous values in our entry now sorted.
		tempValues = tempEntry.values;
		Arrays.sort(tempValues);
		// Writing the sorted tempValue back into the entry.
		tempEntry.values = tempValues;
		System.out.println("ok\n");
	}
		
	public static void snapshot(){
		// THIS METHOD REQUIRES A DEEP CLONE/COPY.
		// Passing the parent entry "database" into the saveSnapshot method, which in turn saves it to an array list.
		// We will also pass an ID with the snapshot which will always be a unique identifier as it simply increments each save.
		// Creating a non-static entry so it can be passed to our array list.
		Entry tempDatabase = new Entry();	
		// All code past here is used for the deep copy.
		// Utilising our set-entry method to do the copy. We will use a for loop.
		Entry currentEntry = new Entry();
		// Starting at the head.
		currentEntry = database.head;
		// Check to see if the database is currently empty, as this is a special save case.
		if(currentEntry == null){
			tempDatabase.snapshotID = snapshotID;
			// Passing to the saveSnapshot method.
			snapshots.saveSnapshot(tempDatabase);
			// Increment the id.
			snapshotID++;
			// Exit the method.
			return;
		}
		while(true){
			tempDatabase.insertEntry(currentEntry.key, currentEntry.values);
			if(currentEntry.next == null){
				break;
			}
			currentEntry = currentEntry.next;
		}
		
		tempDatabase.snapshotID = snapshotID;
		// Passing to the saveSnapshot method.
		snapshots.saveSnapshot(tempDatabase);
		// Increment the id
		snapshotID++;
	}
	
	public static void drop(String[] inputDataSeparated){
		// Parsing the ID to be dropped, and then calling the drop method in the snapshot class.
		int id = Integer.parseInt(inputDataSeparated[1]);
		snapshots.drop(id);
	}
	
	public static void checkout(String[] inputDataSeparated){
		// This method requires a deep copy to meet test case requirements. 
		// This prevents editing of the original snapshot and instead only editing the clone copy.
		// Edits should NOT effect the saved snapshot in the snapshot list.
		// Calling our getter within snapshot to return the specified database snapshot, then setting the current database to said snapshot.
		int id = Integer.parseInt(inputDataSeparated[1]);
		// Creating temp entry to store the snapshot entry.
		Entry tempSnapshot = new Entry();
		// Creating a current entry to assist with deep copy.
		Entry currentEntry = new Entry();
		// Final entry that will be used at the end of the deep copy.
		Entry finalSnapshot = new Entry();
		// Check if the snapshot exists.
		if(snapshots.getSnapshot(id) == null){
			System.out.println("no such snapshot\n");
			return;
		}
		// Calling our snapshot getter.
		tempSnapshot = snapshots.getSnapshot(id);
		// All code past here is used for the deep copy.
		// Starting at the head of the temporary snapshot.
		currentEntry = tempSnapshot.head;
		// We need to account for the case where there are no entries in the snapshot.
		// This if statement is a check for such a case.
		if (currentEntry != null){
			while(true){
				finalSnapshot.insertEntry(currentEntry.key, currentEntry.values);
				if(currentEntry.next == null){
					break;
				}
				currentEntry = currentEntry.next;
			}
		}
		// Setting the snapshot ID number.
		finalSnapshot.snapshotID = tempSnapshot.snapshotID;
		// Setting the snapshot to our database entry.
		database = finalSnapshot;
		System.out.println("ok\n");
	}
	
	public static void rollback(String[] inputDataSeparated){
		// Note: This method was delegated to two separate methods as doing it in just the rollback method
		// produced in in-surmountable bug that I was unable to fix in any other way. Apologies.
		// This method will call the rollback method in our snapshot class, which will
		// in turn return the entry to be rolled back to, and have deleted all other past said point.
		// We will then set the returned entry to the database.
		int id = Integer.parseInt(inputDataSeparated[1]);
		// Creating temp entry to store the snapshot entry.
		Entry tempEntry = new Entry();
		// Check if the snapshot exists.
		if(snapshots.getSnapshot(id) == null){
			System.out.println("no such snapshot\n");
			return;
		}
		// Calling the rollback method.
		tempEntry = snapshots.getSnapshot(id);
		snapshots.rollback(id);
		database = tempEntry;
		System.out.println("ok\n");
	}
	
	public static void purge(String[] inputDataSeparated){
		// First step in the purge is to delete the entry from the current state.
		// Null pointer check to prevent null pointer exception.
		if(database.locateEntry(inputDataSeparated[1]) != null){
			database.deleteEntry(database.locateEntry(inputDataSeparated[1]));
		}
		// Second step is to call the purge method within the snapshot class.
		snapshots.purge(inputDataSeparated[1]);
		System.out.println("ok\n");
	}
	
	public static List<Integer> interHelper(int[] listOne, int[] listTwo){
		// This class is designed to be used in conjunction with the inter method.
		// Creating an array list to hold our inter values as it will dynamically size for each individual case.
		List<Integer> interValues = new ArrayList<>();
		// Sorting the arrays
		Arrays.sort(listOne);
		Arrays.sort(listTwo);
		// Nested for loops to scan each element of the first array with every subsequent element of the second.
		for(int i = 0; i < listOne.length; i++){
			for(int j = 0; j < listTwo.length; j++){
				// Checking if the two elements are equal, and not present in the final array.
				if((listOne[i] == listTwo[j]) & (interValues.contains(listOne[i]) == false)){
					interValues.add(listOne[i]);
				}
			}
		}
		return(interValues);
	}
	
	public static void inter(String[] inputDataSeparated){
		// Assumption: There are two cases in which inter will be called. Case 1, there are two keys. Case 2, there are 3 keys.
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if ((database.locateEntry(inputDataSeparated[1]) == null) && (database.locateEntry(inputDataSeparated[2]) == null)){
			System.out.println("no such key\n");
			return;
		}
		// Creating an array list to hold our inter values as it will dynamically size for each individual case.
		List<Integer> interValues = new ArrayList<>();
		// Fetch our two lists using our getters in entry.
		// We need to take deep copys so as not to edit the original arrays.
		int[] listOne = Arrays.copyOf(database.locateEntry(inputDataSeparated[1]).values, (database.locateEntry(inputDataSeparated[1]).values).length);
		int[] listTwo = Arrays.copyOf(database.locateEntry(inputDataSeparated[2]).values, (database.locateEntry(inputDataSeparated[2]).values).length);
		// Calling our interHelper method.
		interValues = interHelper(listOne,listTwo);
		if (inputDataSeparated.length == 3){
			// Case for only two keys
			Collections.sort(interValues);
			System.out.println(Arrays.toString(interValues.toArray()).replaceAll(",",""));
			System.out.print("\n");
			return;
		}
		// From this point on is the case for 3 keys.
		// Creating a new int array so that we can run a similar comparison
		int[] newInterValuesList = new int[interValues.size()];
		// Populating array with previous values stored in the interValues list.
		for(int i = 0; i < interValues.size(); i++){
			newInterValuesList[i] = interValues.get(i);
		}
		// Clear the inter values list so that we can fill it with the intersection later.
		interValues.clear();
		// Creating list from third key to be passed to helper method.
		int[] listThree = Arrays.copyOf(database.locateEntry(inputDataSeparated[3]).values, (database.locateEntry(inputDataSeparated[3]).values).length);
		// Calling our interHelper method.
		interValues = interHelper(newInterValuesList,listThree);
		// Final print-out.
		Collections.sort(interValues);
		System.out.println(Arrays.toString(interValues.toArray()).replaceAll(",",""));
		System.out.print("\n");
	}
	
	public static List<Integer> diffHelper(int[] listOne, int[] listTwo){
		// This class is designed to be used in conjunction with the diff method.
		// Creating an array list to hold our inter values as it will dynamically size for each individual case.
		List<Integer> diffValues = new ArrayList<>();
		// Sorting the arrays
		Arrays.sort(listOne);
		Arrays.sort(listTwo);
		// We are checking once again if there are equal elements, and if so breaking the loop.
		// If we do not find a match, add the value to the diffValues list.
		// This loop scans forwards -> and checks if any elements in the first array are unique to the second.
		for(int i = 0; i < listOne.length; i++){
			for(int j = 0; j < listTwo.length; j++){
				// If a match is found, break.
				if(listOne[i] == listTwo[j]){
					break;
				}
				// If we make it to the end of the second array, and the entry isn't found AND not present in the final array, add to final array.
				if ((j == (listTwo.length - 1)) & (diffValues.contains(listOne[i]) == false)){
					diffValues.add(listOne[i]);
				}
			}
		}
		// This loop scans forwards -> and checks if any elements in the second array are unique to the first. its functionality is the same
		// as the first loop but the arrays are flipped.
		for(int i = 0; i < listTwo.length; i++){
			for(int j = 0; j < listOne.length; j++){
				if(listTwo[i] == listOne[j]){
					break;
				}
				// If we make it to the end of the second array, and the entry isn't found AND not present in the final array, add to final array.
				if ((j == (listOne.length - 1)) & (diffValues.contains(listTwo[i]) == false)){
					diffValues.add(listTwo[i]);
				}
			}
		}
		return(diffValues);
	}
	
	public static void diff(String[] inputDataSeparated){
		// Similar to inter but does a search backwards and forwards.
		// Assumption: There are two cases in which inter will be called. Case 1, there are two keys. Case 2, there are 3 keys.
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if ((database.locateEntry(inputDataSeparated[1]) == null) && (database.locateEntry(inputDataSeparated[2]) == null)){
			System.out.println("no such key\n");
			return;
		}
		// Creating an array list to hold our inter values as it will dynamically size for each individual case.
		List<Integer> diffValues = new ArrayList<>();
		// Fetch our two lists using our getters in entry.
		// We need to take deep copys so as not to edit the original arrays.
		int[] listOne = Arrays.copyOf(database.locateEntry(inputDataSeparated[1]).values, (database.locateEntry(inputDataSeparated[1]).values).length);
		int[] listTwo = Arrays.copyOf(database.locateEntry(inputDataSeparated[2]).values, (database.locateEntry(inputDataSeparated[2]).values).length);
		// Calling the diffHelper method to fetch the difference of the two arrays.
		diffValues = diffHelper(listOne,listTwo);
		// Output the result.
		if (inputDataSeparated.length == 3){
			// Case for only two keys
			Collections.sort(diffValues);
			System.out.println(Arrays.toString(diffValues.toArray()).replaceAll(",",""));
			System.out.print("\n");
			return;
		}
		// From this point on is the case for 3 keys.
		// Creating a new int array so that we can run a similar comparison
		int[] newDiffValuesList = new int[diffValues.size()];
		// Populating array with previous values.
		for(int i = 0; i < diffValues.size(); i++){
			newDiffValuesList[i] = diffValues.get(i);
		}
		// Clear the diff values list.
		diffValues.clear();
		// Creating array from third key to be passed to helper method.
		int[] listThree = Arrays.copyOf(database.locateEntry(inputDataSeparated[3]).values, (database.locateEntry(inputDataSeparated[3]).values).length);
		// Calling helper method and passing the two new arrays to find the difference.
		diffValues = diffHelper(listThree, newDiffValuesList);
		// Sorting the result.
		Collections.sort(diffValues);
		// Printing results.
		System.out.println(Arrays.toString(diffValues.toArray()).replaceAll(",",""));
		System.out.print("\n");
	}
	
	public static void union(String[] inputDataSeparated){
		// Once again, for union we will assume two possible cases: The user enters 2 keys. The user enters 3 keys.
		// This method is required to create a list of all elements contained in both list one and list two.
		// I've used a hashset here because it was the quickest and easiest solution I could find.
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if ((database.locateEntry(inputDataSeparated[1]) == null) && (database.locateEntry(inputDataSeparated[2]) == null)){
			System.out.println("no such key\n");
			return;
		}
		// Fetching our two input lists and storing in int arrays.
		int[] listOne = Arrays.copyOf(database.locateEntry(inputDataSeparated[1]).values, (database.locateEntry(inputDataSeparated[1]).values).length);
		int[] listTwo = Arrays.copyOf(database.locateEntry(inputDataSeparated[2]).values, (database.locateEntry(inputDataSeparated[2]).values).length);
		// Since hashsetting can only be performed on integer arrays we need to convert.
		Integer[] arrayOne = new Integer[listOne.length];
		Integer[] arrayTwo = new Integer[listTwo.length];
		// Poppulating our new Integer arrays with the information from our int arrays.
		for (int i = 0; i < arrayOne.length; i++){
			arrayOne[i] = listOne[i];
		}
		for (int i = 0; i < arrayTwo.length; i++){
			arrayTwo[i] = listTwo[i];
		}
		// Creating the hashset union that will be used to find the union of the two sets.
		HashSet<Integer> union = new HashSet<>();
		// Now that we have converted, we can fill the hash set and subsequently determine the union.
		union.addAll(Arrays.asList(arrayOne));
        union.addAll(Arrays.asList(arrayTwo));
		// Since we may need to use this data for a third key union, we need to convert from a hashset back to an Integer array.
		Integer[] unionSet = {};
    	unionSet = union.toArray(unionSet);
		
		// If we only had two keys to determine the union of, print the union now.
		if (inputDataSeparated.length == 3){
			System.out.println((Arrays.toString(unionSet) + "\n").replaceAll(",",""));
			return;
		}
		// From this point onwards we are assuming that there are three keys to find the union of.
		int[] listThree = Arrays.copyOf(database.locateEntry(inputDataSeparated[3]).values, (database.locateEntry(inputDataSeparated[3]).values).length);
		Integer[] arrayThree = new Integer[listThree.length];
		for (int i = 0; i < arrayThree.length; i++){
			arrayThree[i] = listThree[i];
		}
		HashSet<Integer> unionTwo = new HashSet<>();
		unionTwo.addAll(Arrays.asList(arrayThree));
        unionTwo.addAll(Arrays.asList(unionSet));
		// The unionTwo hashset now contains the union of our three keys.
		// We can npw convert unionTwo to an Integer array and print the array.
		Integer[] unionSetTwo = {};
    	unionSetTwo = unionTwo.toArray(unionSetTwo);
		System.out.println((Arrays.toString(unionSetTwo) + "\n").replaceAll(",",""));	
	}
	
	public static void cartprod(String[] inputDataSeparated){
		// This method is required to find the cartesian product of 2 - 3 provided arrays.
		// Note: We are assuming that the only two cases this method will be called in are:
		// 1. There are two supplied keys/arrays
		// 2. There are three supplied keys/arrays
		// Check if the entry exists in the database, if not, print a helpful message and exit.
		if ((database.locateEntry(inputDataSeparated[1]) == null) && (database.locateEntry(inputDataSeparated[2]) == null)){
			System.out.println("no such key\n");
			return;
		}
		
		// Fetch our two lists using our getters in entry.
		// We need to take deep copys so as not to edit the original arrays.
		int[] arrayOne = Arrays.copyOf(database.locateEntry(inputDataSeparated[1]).values, (database.locateEntry(inputDataSeparated[1]).values).length);
		int[] arrayTwo = Arrays.copyOf(database.locateEntry(inputDataSeparated[2]).values, (database.locateEntry(inputDataSeparated[2]).values).length);
		
		// Case 1:
		if(inputDataSeparated.length == 3){
			// Print the first outer brace.
			System.out.print("[ ");
			// Nested for loops to match each first element from first array with first from second array, and so on.
			for(int i = 0; i < arrayOne.length; i++){
				for (int j = 0; j < arrayTwo.length; j++){
					System.out.print("[" + arrayOne[i] + " " + arrayTwo[j] + "] ");
				}
			}
			System.out.print("]");
			System.out.println("\n");
			return;
		}
		
		// Case 2:
		if(inputDataSeparated.length == 4){
			// Take a deep copy of the third array.
			int[] arrayThree = Arrays.copyOf(database.locateEntry(inputDataSeparated[3]).values, (database.locateEntry(inputDataSeparated[3]).values).length);
			// Print the first outer brace.
			System.out.print("[ ");
			// Nested for loops to match each first element from first array with first from second array, first with third, and so on.
			for(int i = 0; i < arrayOne.length; i++){
				for (int j = 0; j < arrayTwo.length; j++){
					for (int z = 0; z < arrayThree.length; z++){
						System.out.print("[" + arrayOne[i] + " " + arrayTwo[j] + " " + arrayThree[z] + "] ");
					}
				}
			}
			System.out.print("]");
			System.out.println("\n");
			return;
		}
		// Based on the input assumptions this point should not be reached.
		return;
	}
	
	public static void main(String[] args) {
		Scanner readInput = new Scanner(System.in);
		// Loopcheck will be set to false upon exiting to close the loop and exit the program.
		boolean loopCheck = true;
		while(loopCheck){
			// Reading text from standard input and splitting for later use and analysis
			System.out.print("> ");
			String inputData = readInput.nextLine();
			String[] inputDataSeparated = inputData.split(" ");
			// .equals used instead of == operator as the pointers for both objects were not the same. This produced numerous bugs.
			// Case handler for input commands
			if (inputDataSeparated[0].equalsIgnoreCase("set")){
				set(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("help")){
				help();
			}else if(inputDataSeparated[0].equalsIgnoreCase("bye")){
				// Exit the program, close the loop by changing loopCheck to false.
				bye();
				loopCheck = false;
			}else if(inputDataSeparated[0].equalsIgnoreCase("del")){
				del(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("get")){
				get(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("push")){
				push(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("append")){
				append(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("pick")){
				pick(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("pluck")){
				pluck(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("pop")){
				pop(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("list") && inputDataSeparated[1].equalsIgnoreCase("entries")){
				database.listEntries();
			}else if(inputDataSeparated[0].equalsIgnoreCase("list") && inputDataSeparated[1].equalsIgnoreCase("keys")){
				database.listKeys();
			}else if(inputDataSeparated[0].equalsIgnoreCase("min")){
				minmax(inputDataSeparated,0);
			}else if(inputDataSeparated[0].equalsIgnoreCase("max")){
				minmax(inputDataSeparated,1);
			}else if(inputDataSeparated[0].equalsIgnoreCase("sum")){
				sum(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("len")){
				len(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("rev")){
				rev(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("sort")){
				sort(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("uniq")){
				uniq(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("snapshot")){
				snapshot();
			}else if(inputDataSeparated[0].equalsIgnoreCase("diff")){
				diff(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("list") && inputDataSeparated[1].equalsIgnoreCase("snapshots")){
				snapshots.listSnapshots();
			}else if(inputDataSeparated[0].equalsIgnoreCase("checkout")){
				checkout(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("drop")){
				drop(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("rollback")){
				rollback(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("purge")){
				purge(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("inter")){
				inter(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("diff")){
				diff(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("union")){
				union(inputDataSeparated);
			}else if(inputDataSeparated[0].equalsIgnoreCase("cartprod")){
				cartprod(inputDataSeparated);
			}else{
			}
		}
	}
}
