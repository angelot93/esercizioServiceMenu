package it.omicron.jsonparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;

import it.omicron.entity.MenuContent;
import it.omicron.entity.MenuNode;

public class JsonParser {

	//la variabile statica maxDepth serve a tenere conto di quanto è la massima profondità raggiunta dai nodi figli
	static int maxDepth=0;
	// il contatore rowNum è una variabile statica che tiene conto invece di che numero di riga si sta parlando 
	// al momento della stampa dei contenuti del file Json dentro il foglio excel
	static int rowNum = 0;

	public static void main(String[] args) throws Exception {

		//In questo primo step , un oggetto di tipo properties è stato istanziato al fine di richiamare il file 
		// esercizio.properties e di caricarne le direttive inerenti ai path di cartelle di input e output del programma
		// dalla cartella di input si estrapola il file Json e nella cartella di output viene caricato il file xlsx in formato 
		//excel
		Properties prop= new Properties();
		try {
			prop.load(new FileInputStream("./resources/esercizio.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//In questo step, attraverso la libreria Gson, una libreria esterna fornita da Google, è stato possibile eseguire in 
		// poche istruzioni la lettura del file Json. Tale libreria permette di scorrere il file Json , prenderne i dati e 
		// istanziare un oggetto che abbia le stesse proprietà prese dal file.
		File f= new File(prop.getProperty("input"));
		
		FileReader reader = new FileReader(f);
		Gson g = new Gson();
		//In questo step è stato preso il file Json e trasformato in un oggetto di tipo Menucontent attraverso il metodo
		// "fromJson"
		MenuContent mc = g.fromJson(reader, MenuContent.class);
		List<MenuNode> lista = mc.getNodes();
		
		//creazione della cartella di output dove si va poi a caricare il file excel
		Files.createDirectories(Paths.get(prop.getProperty("output")));
		
		
		// Il metodo in questione è utilizzato per calcolare la profondità massima che i sottonodi riescono a raggiungere.
		// in questo caso vengono passati come argomenti la lista di nodi del MenuContent precedentemente ottenuto in ritorno col metodo della
		//libreria Gson, e la posizione iniziale da cui parte il calcolo della profondità
		calcolaProfondita(lista,0);

		//Qui viene introdotto l'utilizzo di metodi di un'altra libreria , questa volta di Apache , ossia Apache Poi.
		//Tale libreria ha come scopo quello della manipolazione di file Json e il caricamento dei propri dati sottoforma
		// di file formato Microsoft. In questo caso l'esercizio richiedeva che il file Json venisse trasformato in un file
		// di tipo Excel
		
		// viene creato un oggetto di tipo Workbook e uno di tipo sheet che altro non sono che la rappresentazione in OOP di
		// un file excel ex novo, una sorta di tela bianca sulla quale vanno poi inserite di volta in volta le righe
		// La prima riga del file excel non è altro che la riga con le varie intestazioni di colonna, ossia tutte le categorie
		// di dati raggruppate successivamente per tipologia.
		

		XSSFWorkbook wb = new XSSFWorkbook();
		
		// l'esercizio richiedeva che al momento della creazione di un file excel, tale file dovesse assumere il nome
		// "Menu" accompagnato dalla versione del menuContent, presente all'interno di esso
		XSSFSheet sheet = wb.createSheet("Menu "+ mc.getVersion());
		XSSFRow row = sheet.createRow(rowNum);
		
		// dopo aver creato la prima riga, che in teoria parte dalla posizione 0 del file sheet, viene incrementata la variabile
		//rowNum , di modo tale da lasciare intaccata la posizione 0 del file excel e quindi le varie intestazioni di colonna
		rowNum++;

       //Qui avviene la trascrizione delle intestazioni di colonna, attraverso un array di stringhe che deve essere successivamente 
	  // iterato al fine di tracciare in ognuna delle caselle predestinate l'intestazione ad essa corrispondente.
		
		String [] headers= {"ServiceId","NodeName","NodeType","GroupType","FlowType","ResourceId"};
		for(int i = 0 ; i<=maxDepth;i++) {
       
			//Attraverso lo scorrimento dell'array, viene trascritta la stringa di intestazione nella prima riga del file excel
			// che corrispondono agli indicatori sul file excel della profondità alla quale è riferito un determinato nodo
			// la griglia iniziale del file excel è quella sulla quale viene poi definita attraverso una X a che profondità risulta
			// il nodo presente in quella riga. All'inizio chiaramente sono tutte vuote
			row.createCell(i).setCellValue(i);
		}
		// in questo ciclo, iterando dalla cella successiva a quella dell'intestazione, vengono scritti tutti gli altri
	    // valori che fanno parte dell'intestazione stessa, che come è stato detto sono salvati nell'array "headers"
		for(int i = maxDepth +1, j = 0  ; j<headers.length;i++, j++) {

			row.createCell(i).setCellValue(headers[j]);
		}
        // questo è sostanzialmente il metodo che serve a scrivere tutte le righe del file del suddetto excel, il quale 
		//prende in argomento di ingresso l'insieme di sottonodi dei primi due nodi "genitori", il foglio vuoto excel 
		// precedentemente istanziato e l'array di stringhe per intestazione
		creaRighe(mc.getNodes(), sheet, headers);
		
       // tale comando serve sostanzialmente alla visualizzazione corretta delle scritte all'interno della colonna
		// per un migliore layout e display del file Excel e quindi una più facile comprensione all'occhio umano
		for(int i = 0 ; i<sheet.getRow(0).getLastCellNum();i++) {
			sheet.autoSizeColumn(i);
		}
        
		// questo comando sostanzialmente crea il file di output nella cartella specificata nel project.attraverso un 
		//oggetto di tipo FileOutputStream viene scritto il flusso in uscita di dati che confluisce nel file ServiceMenu.xlsx
		
		
			FileOutputStream out = new FileOutputStream(prop.getProperty("filePath"));

		    wb.write(out);
		    out.close();
			wb.close();


	}


	private static void creaRighe(List<MenuNode> lista, XSSFSheet sheet, String[] headers) {
	
		for(MenuNode currentNode:lista) {
			
			// dopo aver già creato la prima riga del foglio excel, quella con tutte le intestazioni di colonna, adesso
			// si passa alla creazione delle righe successive, infatti il contatore rowNum è stato precedentemente incrementato
			// in questo caso viene creata una nuova riga nel ciclo forEach per ogni sottonodo della lista
			XSSFRow row= sheet.createRow(rowNum);

			for (int i = 0; i < maxDepth; i++) {
				// in questo passaggio ,attraverso il controllo , avviene quello che è stato precedentemente accennato ossia
				// viene marcata con una X la profondità corrispondente al nodo che viene tenuto in considerazione in 
				//una specifica iterazione.
				
				if(i==currentNode.getNodeDepth()) {
					row.createCell(i).setCellValue("X");
				}
				else 
				{
					row.createCell(i).setCellValue("");
				}
			}
             // in questo altro ciclo for invece , una volta superata la soglia di 4, che è la massima profondità 
			// raggiunta dai nodi, e quindi una volta superata la colonna di conteggio della profondità , devono essere 
			// trascritte tutte le altre tipologie di dati presenti del Json all'interno del file Excel.
			// la chiamata al metodo getCellValue serve proprio a capire quale tipo di dato deve essere inserito nella cella
			// corrente. Ad esempio se stiamo all'interno della colonna ServiceId , il metodo individua il ServiceId del Json
			//dell'oggetto MenuNode corrispondente a quella riga e lo trascrive nell'excel
			for (int i = maxDepth+1; i <= headers.length + maxDepth  ; i++) {
				row.createCell(i).setCellValue(getCellValue(headers[i-maxDepth-1], currentNode));
			}	
			rowNum++;
			
			// questa chiamata al metodo corrente, rende il metodo stesso ricorsivo. In questo modo è possibile facilitare la
			//stesura del codice con meno righe di comando. Nel momento in cui ci sono ancora altri sottonodi da scorrere, il 
			//metodo si richiama ricorsivamente fino a che la condizione dell'if viene rispettata.
			if(currentNode.getNodes() != null && !currentNode.getNodes().isEmpty()) {
				creaRighe(currentNode.getNodes(), sheet, headers);
			}
		}
	}


	private static String getCellValue(String column, MenuNode currentNode) {

		// Tramite questo metodo viene definita una serie di casistiche per cui se ci si trova in corrispondenza di una
		// determinata colonna di intestazione, deve essere estrapolato il dato corrispondente di modo tale da poterlo 
		//inserire correttamente in termini logici all'interno della tabella Excel. Chiaramente ogni colonna del file excel
		//deve avere tutti i dati che si riferiscono ad una specifica categoria di dato presente nel Json, ad esempio tutti i 
		//dati di tipo NodeType dei vari oggetti menuNode. Mentre la riga deve contenere tutta la gamma di dati appartenenti 
		// allo stesso oggetto, ad esempio la prima riga del file excel,dopo quella di intestazione, deve contenere tutte le 
		//informazioni dell'oggetto MenuNode chiamato Ricariche nel contesto del file Json.
		
		switch (column) {
		case "ServiceId": return ""+currentNode.getNodeId(); 
		case "NodeName" : return currentNode.getNodeName(); 
		case "NodeType" : return currentNode.getNodeType(); 
		case "GroupType": return currentNode.getGroupType(); 
		// le ultime chiamate utilizzano programmazione funzionale. Se il contenuto del FlowType e Resource all'interno del
		// sottonodo è diverso da Null allora vengono eseguite le operazioni successive al punto interrogativo
		case "FlowType" : return currentNode.getFlowType()!=null?currentNode.getFlowType():""; 
		case "ResourceId": return currentNode.getResource()!=null?""+currentNode.getResource().getId():""; 

		default: return null;

		}



	}

	private static void calcolaProfondita(List<MenuNode> lista,int currentDepth) {

		// anche qui la profondità massima dei nodi viene calcolata ricorsivamente.Le prime righe del metodo indicano la condizione 
		// primaria attraverso la quale la ricorsione avvia il suo processo. Tale condizione serve a dare una fine al
		// loop ricorsivo che altrimenti andrebbe avanti all'infinito senza risoluzione. Se la currentDepth è maggiore della massima
		//profondità possibile , viene assegnato tale valore alla variabile maxDepth che è statica e quindi salvata in una locazione
		// di memoria statica indipendente dal metodo. Scorrendo tutta la lista di MenuNode si ripete il processo di conteggio
		//e se non si soddisfa tale condizione iniziale, il metodo richiama nuovamente se stesso passando in argomento la lista
		// di nodi successivi.
	
		if(currentDepth>maxDepth) {
			maxDepth=currentDepth;
		}
		for (MenuNode currentNode : lista) {
			currentNode.setNodeDepth(currentDepth);
			if(currentNode.getNodes()!= null && !currentNode.getNodes().isEmpty()) {

				calcolaProfondita(currentNode.getNodes(),currentDepth+1);
			}

		}

	}


}
