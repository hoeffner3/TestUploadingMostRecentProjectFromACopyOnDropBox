package org.processmining.plugins.tracealignmentwithguidetree.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.processmining.plugins.tracealignmentwithguidetree.enumtypes.TraceBack;

/**
 * @author R. P. Jagadeesh Chandra Bose (JC)
 * @date 02 July 2009
 * @email j.c.b.rantham.prabhakara@tue.nl
 * @version 1.0
 * 
 */
public class ArrayMatrixFileIO {

	public ArrayMatrixFileIO() {

	}

	public int[][] readIntMatrixFromFile(String inputDir, String fileName) {
		int[][] data;
		List<String> dataStringList = new ArrayList<String>();

		try {
			String currentLine;

			BufferedReader reader = new BufferedReader(new FileReader(inputDir + "\\" + fileName));

			while ((currentLine = reader.readLine()) != null) {
				currentLine = currentLine.replaceAll("\\[", "");
				currentLine = currentLine.replaceAll("\\]", "");
				dataStringList.add(currentLine);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Exception while Reading: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		}

		//Put the data in the array list in the matrix
		data = new int[dataStringList.size()][];
		String[] dataStringSplit;
		int index = 0;
		for (String dataString : dataStringList) {
			dataStringSplit = dataString.split(",");
			data[index] = new int[dataStringSplit.length];
			for (int i = 0; i < dataStringSplit.length; i++) {
				data[index][i] = new Integer(dataStringSplit[i]).intValue();
			}
			index++;
		}

		return data;
	}

	public float[][] readFloatMatrixFromFile(String inputDir, String fileName) {
		float[][] data;
		List<String> dataStringList = new ArrayList<String>();

		try {
			String currentLine;

			BufferedReader reader = new BufferedReader(new FileReader(inputDir + "\\" + fileName));

			while ((currentLine = reader.readLine()) != null) {
				currentLine = currentLine.replaceAll("\\[", "");
				currentLine = currentLine.replaceAll("\\]", "");
				dataStringList.add(currentLine);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Exception while Reading: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		}

		//Put the data in the array list in the matrix
		data = new float[dataStringList.size()][];
		String[] dataStringSplit;
		int index = 0;
		for (String dataString : dataStringList) {
			dataStringSplit = dataString.split(",");
			data[index] = new float[dataStringSplit.length];
			for (int i = 0; i < dataStringSplit.length; i++) {
				data[index][i] = new Double(dataStringSplit[i]).floatValue();
			}
			index++;
		}

		return data;
	}

	public void writeToFile(String dir, String fileName, float[][] matrixT) {
		FileOutputStream fos;
		PrintStream ps;
		DecimalFormat formatter = new DecimalFormat("0.00");
		if (isDirExists(dir)) {
			try {
				fos = new FileOutputStream(dir + "\\" + fileName);
				ps = new PrintStream(fos);
				int noRows = matrixT.length;
				int noCols = matrixT[0].length;

				for (int i = 0; i < noRows; i++) {
					for (int j = 0; j < noCols - 1; j++) {
						ps.print(formatter.format(matrixT[i][j]) + ",");
					}
					ps.print(formatter.format(matrixT[i][noCols - 1]));
					ps.println();
				}

				ps.close();
				fos.close();
			} catch (FileNotFoundException e) {
				System.err.println("File Not Found Exception while creating file: " + dir + "\\" + fileName);
				System.exit(0);
			} catch (IOException e) {
				System.err.println("IO Exception while writing file: " + dir + "\\" + fileName);
				System.exit(0);
			}
		} else {
			System.err.println("Can't create Directory: " + dir);
		}
	}

	public void writeToFile(String dir, String fileName, int[][] matrixT) {
		FileOutputStream fos;
		PrintStream ps;

		if (isDirExists(dir)) {
			try {
				fos = new FileOutputStream(dir + "\\" + fileName);
				ps = new PrintStream(fos);
				int noRows = matrixT.length;
				int noCols = matrixT[0].length;

				for (int i = 0; i < noRows; i++) {
					for (int j = 0; j < noCols - 1; j++) {
						ps.format("%5d", matrixT[i][j]);
					}
					ps.format("%5d", matrixT[i][noCols - 1]);
					ps.println();
				}

				ps.close();
				fos.close();
			} catch (FileNotFoundException e) {
				System.err.println("File Not Found Exception while creating file: " + dir + "\\" + fileName);
				System.exit(0);
			} catch (IOException e) {
				System.err.println("IO Exception while writing file: " + dir + "\\" + fileName);
				System.exit(0);
			}
		} else {
			System.err.println("Can't create Directory: " + dir);
		}
	}

	public void printTraceBack(String[] seq1, String[] seq2, TraceBack[][] T){
		int noRows = seq1.length+1;
		int noCols = seq2.length+1;
		
		System.out.print("\t");
		for(int j = 1; j < noCols; j++){
			System.out.print(seq2[j-1]+"\t");
		}
		System.out.println();
		
		for(int i = 0; i < noRows; i++){
			for(int j = 0; j < noCols; j++){
				if(T[i][j] == TraceBack.DIAGONAL)
					System.out.print("\\\t");
				else if(T[i][j] == TraceBack.UP)
					System.out.print("|\t");
				else
					System.out.print("--\t");
			}
			System.out.println();
		}
	}
	
	/*
	 * public void writeToFile(String dir, String fileName, TraceBack[][]
	 * matrixT){ FileOutputStream fos; PrintStream ps;
	 * 
	 * if (isDirExists(dir)) { try { fos = new FileOutputStream(dir + "\\" +
	 * fileName); ps = new PrintStream(fos); int noRows = matrixT.length; int
	 * noCols = matrixT[0].length;
	 * 
	 * for(int i = 0; i < noRows; i++){ for(int j = 0; j < noCols; j++){
	 * if(matrixT[i][j] == TraceBack.DIAGONAL) ps.print("\\  ,"); else
	 * if(matrixT[i][j] == TraceBack.UP) ps.print("|  ,"); else if(matrixT[i][j]
	 * == TraceBack.LEFT) ps.print("-- ,"); else ps.print("   ,");
	 * 
	 * } // ps.print(formatter.format(matrixT[i][noCols-1])); ps.println(); }
	 * 
	 * ps.close(); fos.close(); } catch (FileNotFoundException e) { System.err
	 * .println("File Not Found Exception while creating file: " + dir + "\\" +
	 * fileName); System.exit(0); } catch (IOException e) {
	 * System.err.println("IO Exception while writing file: " + dir + "\\" +
	 * fileName); System.exit(0); } } else {
	 * System.err.println("Can't create Directory: " + dir); } }
	 */
	public void createDir(String dir) {
		if (!(new File(dir)).exists()) {
			boolean success = new File(dir).mkdirs();
			if (!success) {
				System.out.println("Cannot create directory: " + dir);
				System.exit(0);
			}
		}
	}

	private boolean isDirExists(String dir) {
		if (!(new File(dir)).exists()) {
			return new File(dir).mkdirs();
		} else {
			return true;
		}
	}
}
