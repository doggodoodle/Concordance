import java.io.File;
import java.io.FileNotFoundException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
/**
 * This program takes the filename of an English text document along with filepath as argument, and prints the Concordance results of the file.
 * @author ritvikmathur
 */

public class Concordance {
	
	private String text;
	
	private HashMap<String,Word> map = new HashMap<String,Word>();
	
	private static class Word{
		/*
		 * Word is an inner class holding frequency and sentenceNum details of the word.
		 * The toString method of this class is overriden to enable printing in desired format.
		 */
		private String word=null;
		private Integer frequency=null;
		private ArrayList<Integer> sentences=null;
		@Override
		public String toString() {
			return "{"+frequency+":"+listToString(sentences)+"}";
		}		
		public static String listToString(List<?> list) {
		    StringBuffer sb = new StringBuffer();
		    for (int i = 0; i < list.size(); i++) {
		    	if(i==0)
		    		sb.append(list.get(i));
		    	else
		    		sb.append(","+list.get(i));
		    }
		    return sb.toString();
		}
		
	}
	
	public static void main(String[] args) {		
		Concordance app = new Concordance();
		if(args.length!=1){			
			System.out.println("Invalid arguments! Please enter only one argument with file name along with path as argument.");
			System.exit(0);
		}
		app.start(args[0]);	
	}
	
	private void start(String fileName){
		if(readFileToString(fileName)){
			extract();
			print();
		}
		else{
			System.out.println("Operation failed! Exiting.");
			System.exit(0);
		}
	}
	
	private boolean readFileToString(String fileName){
		/*
		 * This method reads the contents of the file and stores them in a String variable.
		 */
		try {
			File file = new File(fileName);
			if(!file.exists()){
				System.out.println("File Not Found! Please enter valid file name along with path in the argument.");
				return false;
			}
			if(file.length()==0){
				System.out.println("File Empty!");
				return false;				
			}
			//System.out.println("Reading file..");
			text = new Scanner(file).useDelimiter("\\Z").next().replaceAll("(\\r|\\n)", " ");			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void print(){
		/*
		 * This method is to print out the read contents after sorting and formatting.
		 * It uses a list to get keySet from the map, and sorts the list. This sorted list is then used to print contents of the HashMap.
		 */
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(map.keySet());
		Collections.sort(list);	
		printSeq(list.size(),list);
	}
	
	public void printSeq(Integer length, ArrayList<String> list){
		/*
		 * This method is to enable the printing of contents with required alphabetical sequence from a to z to zz and so on.
		 */
		char c = (char)('a');	
		int k = 0;
		for(int i = 0; i < length; i++) {
			if(i/26>k){
				k=i/26;
				c = (char)('a');	
			}
	        System.out.println(concat(k,c)+". "+list.get(i)+"\t"+map.get(list.get(i)));
	        c++;
	    }
	}
	private String concat(int k, char c) {
		if(k==0)
			return Character.toString(c);
		else{
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<=k;i++){
				sb.append(c);
			}
			return sb.toString();
		}		
	}
	
	private void extract() {
		/*
		 * This method uses the getSentenceInstance and getWordInstance of the BreakIterator class, to iterate over the file contents stored in the String variable.
		 * BreakIterator.getSentenceInstance is used to keep track of the sentence numbers of the words.
		 * The words extracted from sentences using BreakIterator.getWordInstance, are then stored in a HashMap along with frequency and sentence number. 
		 */
		Locale currentLocale = new Locale ("en","US");
		BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(currentLocale);
		sentenceIterator.setText(text);
	    int startS = sentenceIterator.first();
	    int endS = sentenceIterator.next();
	    int sentenceNum = 1;
	    while (endS != BreakIterator.DONE) {
	        String sentence = text.substring(startS,endS);
	        BreakIterator wordIterator = BreakIterator.getWordInstance(currentLocale);
	        wordIterator.setText(sentence);
	        int startW = wordIterator.first();
	        int endW = wordIterator.next();
	        while (endW != BreakIterator.DONE) {
	            String word = sentence.substring(startW,endW);
	            if (Character.isLetterOrDigit(word.charAt(0))) {
	            	//***Storing words to HashMap***
	                store(word.toLowerCase(),sentenceNum);
	            }
	            startW = endW;
	            endW = wordIterator.next();
	        }
	        sentenceNum++;
	        startS = endS;
	        endS = sentenceIterator.next();
	    }
	}
	
	private void store(String s,Integer sentenceNum){
		/*
		 * This method takes in the words to be stored to the HashMap<String, Word>, where Word is an inner class holding frequency and sentenceNum details of the word.
		 * It checks the HashMap for previous existence of the word to update the frequency, and adds the sentenceNum to a list in the Word object.		 *
		 */
		if(map.get(s)!=null){
			Word w = map.get(s);
			w.frequency++;
			w.sentences.add(sentenceNum);
			map.put(s, w);
		}
		else{
			Word w = new Concordance.Word();
			w.word = s;
			w.frequency=1;
			w.sentences=new ArrayList<Integer>( Arrays.asList(sentenceNum));
			map.put(s, w);
		}
	}

}
