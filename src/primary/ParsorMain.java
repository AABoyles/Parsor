package primary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ParsorMain {

	public static void main(String[] args) throws Exception{

		File[] files;
		StringBuilder text, sentence;
		String line, title = "";
		int sentenceNumber=0, numSentences=1, initialLineLength=0;
		double sensitivity = .3;
		BufferedReader reader = null;

		JFileChooser fileopen = new JFileChooser();
		fileopen.addChoosableFileFilter(new FileNameExtensionFilter("HTML files", "htm", "html"));
		fileopen.setMultiSelectionEnabled(true);
		int ret = fileopen.showDialog(null, "Open file");

		if (ret == JFileChooser.APPROVE_OPTION){
			JOptionPane.showMessageDialog(null,"All Files Loaded Successfully.");

			JFileChooser filesave = new JFileChooser();
			filesave.addChoosableFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
			int ret2 = filesave.showSaveDialog(null);

			if (ret2 == JFileChooser.APPROVE_OPTION){
				PrintStream p=new PrintStream(new FileOutputStream(filesave.getSelectedFile()));
				files = fileopen.getSelectedFiles();

				for(int i = 0; i < files.length; i++){

					text = new StringBuilder("");
					sentenceNumber=0;
					numSentences=1;
					reader = new BufferedReader(new FileReader(files[i]));
					line = reader.readLine().trim();

					while (line!=null){
						initialLineLength = line.length();
						if(line.contains("<title>") && line.contains("</title>")){title = line.substring(line.indexOf("<title>")+7, line.indexOf("</title>"));}
						else{text.append(" " + line.trim());}
						line = reader.readLine();}

					text = new StringBuilder(text.toString().replaceAll("<br />", ". ")
							.replaceAll("<br>", ". ").replaceAll("[	\t]", ". ")
							.replaceAll("<BR>", ". ").replaceAll("&nbsp;", " "));

					if(text.toString().contains("<title>") && text.toString().contains("</title>")){
						title = text.toString().substring(text.toString().indexOf("<title>")+7, 
								text.toString().indexOf("</title>"));}

					while(text.indexOf("<script")!=-1 && text.indexOf("</script>", text.indexOf("<script"))!=-1){
						text.delete(text.indexOf("<script"), text.indexOf("</script>", text.indexOf("<script"))+9);}

					while(text.indexOf("<!--")!=-1 && text.indexOf("-->", text.indexOf("<!--"))!=-1){
						text.delete(text.indexOf("<!--"), text.indexOf("-->", text.indexOf("<!--"))+3);}

					while(text.indexOf("<style")!=-1 && text.indexOf("</style>", text.indexOf("<style"))!=-1){
						text.delete(text.indexOf("<style"), text.indexOf("</style>", text.indexOf("<style"))+8);}

					while(text.indexOf("<")!=-1&&text.indexOf(">", text.indexOf("<"))!=-1){
						text.delete(text.indexOf("<"), text.indexOf(">", text.indexOf("<"))+1);}

					while(text.indexOf("&")!=-1&&text.indexOf(";", text.indexOf("&"))!=-1){
						text.delete(text.indexOf("&"), text.indexOf(";", text.indexOf("&"))+1);}					

					if (text.length() > sensitivity * initialLineLength){

						text=new StringBuilder(text.toString());
						
						for(int j = 0; j < text.toString().split("[.!?][ \n\r]").length; j++){
							sentence = new StringBuilder(text.toString().split("[.!?][ \n\r]")[j]);
							if(sentence.toString().split("\\s").length>3){numSentences++;}}

						if(!title.isEmpty()){
							p.println(
									numSentences + ",1," + ++sentenceNumber + "," 
									+ title.trim().split(" ").length + "," 
									+ title.trim().length() + "," 
									+ title.trim() + ","
									+ files[i].getAbsolutePath());}

						for(int j = 0; j < text.toString().split("[.!?][ \n\r]").length; j++){
							sentence = new StringBuilder(text.toString().split("[.!?][ \n\r]")[j]);
							if(sentence.toString().split("\\s").length>3){
								p.println(
										numSentences + ",0," + ++sentenceNumber + "," 
										+ sentence.toString().trim().split("\\s").length + "," 
										+ sentence.toString().trim().length() + "," 
										+ sentence.toString().trim() + ","
										+ files[i].getAbsolutePath());
							}
						}
					}
				}
				JOptionPane.showMessageDialog(null,"All Done.");
				p.close();
			}
		}
	}
}