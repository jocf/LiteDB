import java.util.Arrays;
public class Entry {
	String key;
	int snapshotID;
	int[] values;
	Entry next;
	Entry previous;
	Entry current;
	Entry oldCurrent;
	Entry tail;
	Entry head;
	
	public void insertEntry(String key, int[] values){
		/* INSERTION AND LIST
		   CREATION METHOD
		   DO NOT EDIT */
		
		// Creating the new entry
		Entry entry = new Entry();
		// Assigning the entries key and attributed values
		entry.key = key;
		entry.values = values;
		// Checking if new entry should be a head element, if so, set to head and exit.
		if (this.head == null){
			// Initialisation of the linked list. This only needs to be run once.
			head = entry;
			// Current is the most recently added entry.
			current = entry;
			// Old current is the most recently added entry from the PREVIOUS time that insertEntry was called.
			oldCurrent = entry;
		}else{
			// Assigning the oldCurrent variable to the previous current, as the new current will be updated at the end of the method.
			oldCurrent = current;
			// We are setting the current entry (entry)'s previous value to the OLD current, and assigning our previous entry's next to the current entry.
			entry.previous = oldCurrent;
			oldCurrent.next = entry;	
			// Updating the current variable
			current = entry;
			// Updating the tail of the list to the new element
			tail = entry;
		}

	}
	
	public void deleteEntry(Entry entry){
		// Return type has been set to Entry in case this method will need to be used in conjunction with other methods. This may change later.
		// This method must have an entry passed to it to delete the entry. Use locateEntry in conjunction with this method for easiest use.
		// Deletetion case if entry is the head.
		if (entry == head){
			// If no elements past head simply set head to null.
			if (head.next == null){
				head = null;
			}else{
			// If elements past head, just set the head.next element to the new head. 
				head = head.next;
				head.previous = null;
			}
			
		// Deletion case if entry is the tail.
		}else if(entry == tail){
			// If there are only head and tail elements, just unlink the tail from the head entry.
			if (tail.previous == head){
				head.next = null;
				current = head;
			}else{
			// For every other case, just set the tail.previous to the new tail.
				tail = tail.previous;
				tail.next = null;
				// Update the new "current" (most recently added entry) to the new tail (previous element) 
				current = tail;
			}
			
		}else{
		// Regular deletion case for an entry in between two entries.
			// Creating temp entries
			Entry tempEntryNext = new Entry();
			Entry tempEntryPrevious = new Entry();
			// Assigning values to temp entries so that we can re-link the list 'database'.
			tempEntryNext = getNext(entry);
			tempEntryPrevious = getPrevious(entry);
			// Re-linking the list
			tempEntryPrevious.next = tempEntryNext;
			tempEntryNext.previous = tempEntryPrevious;
			// Disconnecting next and previous of the entry to be deleted.
			entry.next = null;
			entry.previous = null;
			// Returning the removed entry
		}
	}
	
	public Entry locateEntry(String key){
		// This method is being created to use in multiple other methods that require a search of the list.
		// Checking if the list is empty and returning the head (null) if it is
		if (head == null) {
          //  System.out.println("There are no entries in the database!");
			return head;
        }else{
			// Creating a temporary entry and starting at the head. We will start sarching from the head.
			Entry tempEntry = head;
        	while (tempEntry != null) {
            	if ((tempEntry.key).equals(key)){
					// If they entries key matches, return the entry. The search has completed.
					return tempEntry;
				}
				// Move to the next entry in the database
            	tempEntry = tempEntry.next;
       		}	
		}
		// This point will be reached if the search key is not in the list
		//System.out.println("There is no entry assigned to the search key!");
		return null;
	}
	
	// Lists all the entries in the database.
    public void listEntries() {
		// If there are no entries, print no entries.
        if (head == null) {
           System.out.println("no entries\n");
			// If the element is only the head, print the heads data.
		}else if(head.next == null && head != null){
			System.out.print(head.key + " ");
			System.out.println((Arrays.toString(head.values)).replaceAll(",",""));
			System.out.print("\n");
        }else{
			// Loop through each element of the database printing the key, and the corresponding values stored.
			// Creating a temp entry to print and setting to tail (end) of the database.
			Entry tempEntry = tail;
        	while (tempEntry != null) {
            	System.out.print(tempEntry.key + " ");
				System.out.println((Arrays.toString(tempEntry.values)).replaceAll(",",""));
				// Moving to the previous entry in the database.
            	tempEntry = tempEntry.previous;
       		}	
			System.out.print("\n");
		}
    }
	
	// Lists all keys in the database (very similar to listEntries).
	public void listKeys(){
		// If there are no entries, print no entries.
        if (head == null) {
           System.out.println("no keys\n");
		// If the element is only the head, print the heads data.
		}else if(head.next == null && head != null){
			System.out.print(head.key);
			System.out.println("\n");
        }else{
			// Loop through each element of the database printing the key.
			// Creating a temp entry to print and setting to tail (end) of the database.
			Entry tempEntry = tail;
        	while (tempEntry != null) {
            	System.out.println(tempEntry.key);
				// Moving to the previous entry in the database.
            	tempEntry = tempEntry.previous;
       		}	
			System.out.print("\n");
		}
	}
	
	// Gets the next entry from a given entry and returns it.
	public Entry getNext(Entry entry){
		if (entry.next != null){
			return entry.next;
		}else{
			//System.out.println("There is no next entry!");
			return entry;
		}
	}
	
	// Gets the previous entry from a given entry and returns it.
	public Entry getPrevious(Entry entry){
		if (entry.previous != null){
			return entry.previous;
		}else{
			//System.out.println("There is no previous entry! This element is the head!");
			return entry;
		}
	}
	
}
