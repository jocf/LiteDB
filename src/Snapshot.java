import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
public class Snapshot {
	// Array list of entries to store the individual snapshots.
	List<Entry> snapshotList = new ArrayList<>();
	public void saveSnapshot(Entry passedEntry){
		// Saving the state to our array list.
		snapshotList.add(passedEntry);
		System.out.println("saved as snapshot " + (snapshotList.get(snapshotList.size() - 1)).snapshotID + "\n");
	}
	
	public void listSnapshots(){
		// Loop through the snapshots list and print each snapshotID for each entry visited.
		// We need to consider the special case where there are no snapshots saved.
		if (snapshotList.size() == 0){
			System.out.println("no snapshots\n");
			return;
		}
		for (int i = (snapshotList.size() - 1); i >= 0; i--){
			System.out.println((snapshotList.get(i)).snapshotID);
		}
		System.out.print("\n");
	}
	
	public void drop(int id){
		// To implement our drop method, we will iterate through each value in the array list and check the ID at the most recently visited member.
		// If the ID matches, snapshotList.remove();
		Iterator itr = snapshotList.iterator();
		while (itr.hasNext()) 
        {
			Entry tempEntry = new Entry();
			tempEntry = (Entry)itr.next();
            if (tempEntry.snapshotID == id){
				itr.remove();
				System.out.println("ok\n");
				return;
			}
        }
		// this point will be reached if the snapshot was not found in the loop.
		System.out.println("no such snapshot\n");
	}
	
	public Entry getSnapshot(int id){
		// We will iterate through each element in the ArrayList until we find a matching key, then return that database.
		Iterator itr = snapshotList.iterator();
		while (itr.hasNext()) 
        {
			Entry tempEntry = new Entry();
			tempEntry = (Entry)itr.next();
            if (tempEntry.snapshotID == id){
				return(tempEntry);
			}
        }
		return null;
	}
	
	public void rollback(int id){
		// First, we try to get snapshot, if the ID supplied doesn't work, exit.
		// Else, return the id so the database can be restored to that point, and delete all other snapshots past said point.
		int listSize = snapshotList.size();
		// Loop through each element in the array to check if the ID matches the supplied, if not, remove that entry.
		for(int i = (listSize - 1); i >= 0; i--)
		{
  	  		if ((snapshotList.get(i)).snapshotID == id){
				return;
			}
			snapshotList.remove(i);
		}
		// This point should never be reached since all conditions are met previously.
		return;
	}
	
	public void purge(String key){
		// This method loops through the arraylist and removes the desired key from each entry in the list.
		// Sizing the arraylist
		int listSize = snapshotList.size();
		// Looping through each element, and then removing the desired key.
		for(int i = 0; i < listSize; i++){
			// Null pointer check to prevent null pointer exception
			if(snapshotList.get(i).locateEntry(key) != null){
				(snapshotList.get(i)).deleteEntry(snapshotList.get(i).locateEntry(key));
			}
		}
	}
}
