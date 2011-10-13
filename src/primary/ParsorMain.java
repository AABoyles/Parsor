package primary;
//Parsor - A simple scraper for HTML and text files, which generates a dataset based on the sentences each file contains.
//by Tony Boyles - AABoyles@gmail.com - http://nortalktoowise.com/
/*  This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

<http://www.gnu.org/licenses/>.*/

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
		String[] meta;
		StringBuilder text, sentence;
		String line, title = "";
		int sentenceNumber=0, numSentences=1, initialLineLength=0;
		double sensitivity = .3;
		BufferedReader reader = null;
		JFileChooser fileopen = new JFileChooser();

		fileopen.addChoosableFileFilter(new FileNameExtensionFilter("HTML files", "htm", "html"));
		fileopen.setMultiSelectionEnabled(true);

		JFileChooser filesave = new JFileChooser();
		filesave.addChoosableFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
		int ret = fileopen.showDialog(null, "Open file");

		if (ret == JFileChooser.APPROVE_OPTION){
			JOptionPane.showMessageDialog(null,"All Files Loaded Successfully.");
			int ret2 = filesave.showSaveDialog(null);
			if (ret2 == JFileChooser.APPROVE_OPTION){
				PrintStream p=new PrintStream(new FileOutputStream(filesave.getSelectedFile()));
				files = fileopen.getSelectedFiles();

				for(int i = 0; i < files.length; i++){

					text = new StringBuilder("");
					sentenceNumber=0;
					numSentences=1;
					//meta = files[i].getName().split("_");
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

						text=new StringBuilder(text.toString()
								.replaceAll("\\.\\.\\.", "\\. ").replaceAll("\"", "")
								.replaceAll(",", "").replaceAll("St\\. ", "St ").replaceAll("   ", "\\. ")
								.replaceAll("U\\.S\\. ", "US ").replaceAll("U\\.N\\. ", "UN ")
								.replaceAll("N\\.Y\\. ", "NY ").replaceAll("D\\.C\\. ", "DC ")
								.replaceAll("[A-Z]\\. ", " ").replaceAll("[\r\n\t]", "\\. ")
								.replaceAll("Rep\\. ", "Rep ").replaceAll("Sen\\. ", "Sen ")
								.replaceAll("Sens\\. ", "Senators ").replaceAll("Reps. ", "Representatives ")
								.replaceAll("Wash\\. ", "Wash ").replaceAll("Mass\\. ", "Mass ")
								.replaceAll("[“”]", "''"));

						for(int j = 0; j < text.toString().split("[.!?][ \n\r]").length; j++){
							sentence = new StringBuilder(text.toString().split("[.!?][ \n\r]")[j]);
							if(sentence.toString().split("\\s").length>3){numSentences++;}}

						if(!title.isEmpty()){
							p.println(
									//meta[0] + "," + meta[3] + "," + meta[1] + "," 
									//+ meta[2] + "," + meta[4].charAt(0) + ","
									numSentences + ",1," + ++sentenceNumber + "," 
									+ title.trim().split(" ").length + "," 
									+ title.trim().length() + "," 
									+ title.trim() + ","
									+ files[i].getAbsolutePath());}

						for(int j = 0; j < text.toString().split("[.!?][ \n\r]").length; j++){
							sentence = new StringBuilder(text.toString().split("[.!?][ \n\r]")[j]);
							if(sentence.toString().split("\\s").length>3){
								p.println(
										//meta[0] + "," + meta[3] + "," + meta[1] + "," 
										//+ meta[2] + "," + meta[4].charAt(0) + ","
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
