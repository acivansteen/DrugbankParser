package msk.drugbank4;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;



public class DrugbankTargetParser {
	
	private IDMapper mapper;
	private File outputNodes;
	private File outputEdges;
	private File input;
	
	public DrugbankTargetParser(File bridgeDbFile, File outputNodes, File outputEdges, File input) throws Exception {
		// setting up bridgedb
		DataSourceTxt.init();
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
		mapper = BridgeDb.connect("idmapper-pgdb:" + bridgeDbFile.getAbsolutePath());
		
		this.input = input;
		this.outputEdges = outputEdges;
		this.outputNodes = outputNodes;
	}
	
	public void parseDrugs() throws Exception {
		System.out.println("Parse DrugBank");
		DrugBankParser p = new DrugBankParser();
		Set<DrugModel> res = p.parse(input);
		
		BufferedWriter writerE = new BufferedWriter(new FileWriter(outputEdges));
		BufferedWriter writerN = new BufferedWriter(new FileWriter(outputNodes));
		
		//i chose map here, not sure what is most convenient, hashmap/hashset
		//nodes = targets
		Map<String, String> nodes = new HashMap<String,String>();
		Map<String, DrugModel> drugs = new HashMap<String, DrugModel>();
		
		writerN.write("Identifier\tName\tType\tCategory\n");
		writerE.write("Source\tTarget\n");
		System.out.println("Retrieve interactions for " + res.size() + " drugs.");
		for(DrugModel drug : res){
			for(String group: drug.getGroups()){
				
				if(group.equals("approved")){
					drugs.put(drug.getDrugbankID(), drug);
					Set<String> targets = new HashSet<String>();
					//mapping drugs by DrugbankID			
					for(TargetModel target:drug.getTargets()){
						DataSource ds = DataSource.getExistingBySystemCode("S");
						if (!target.getUniprotId().equals("")) {
							Xref xref= new Xref(target.getUniprotId(),ds);
							//String targetName = target.getName();
							//mapping all the drug targets to Ensembl ID
							Set<Xref> result = mapper.mapID(xref, DataSource.getExistingBySystemCode("En"));
							
							for(Xref x:result){
								if(x.getId().startsWith("ENSG")) {
									
									// filter double edges
									if(!targets.contains(x.getId())) {
										// add edge
										writerE.write(drug.getDrugbankID()+"\t"+ x.getId()+"\n");
										targets.add(x.getId());
									}
									if(!nodes.containsKey(x.getId())) {
										Set<Xref> hgncResult = mapper.mapID(x, DataSource.getExistingBySystemCode("H"));
										
										String tName = new String();
										if(hgncResult.size() > 0) {
											tName= (hgncResult.iterator().next().getId());
										}
										nodes.put(x.getId(),tName);
									}
								}
							}
						}					
					}
				}
			}	
		}
		
		for(String drug: drugs.keySet()) {
			String drugProperties= drugs.get(drug).getCategories().toString().replace("[", "").replace("]", "");
			String drugname = drugs.get(drug).getName();
			if(drugProperties.equals("")) drugProperties = "0";
			writerN.write(drug+"\t"+drugname+"\tDrug\t"+drugProperties+"\n");
		}
		for(String key:nodes.keySet()){
			writerN.write(key+"\t"+nodes.get(key)+"\tGene\t0\n");
		} 		
		writerE.close();
		writerN.close();
		
		System.out.println("Parsing done.");
	}
	
	public static void main(String[] args) throws Exception {
		File bridgedb = new File("C:\\Users\\martina.kutmon\\Workspace\\bram\\Hs_Derby_Ensembl_79_v.01.bridge");
		File edgeOut = new File("drugEdges.txt");
		File nodeOut = new File("drugNodes.txt");
		File input = new File("drugbank.xml");
		DrugbankTargetParser parser = new DrugbankTargetParser(bridgedb, nodeOut, edgeOut, input);
		parser.parseDrugs();
	}
}

