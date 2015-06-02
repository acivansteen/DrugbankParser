package msk.drugbank4;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.jdom2.JDOMException;



public class DrugbankTargetParser {

	public static void main(String[] args) throws JDOMException, IOException, ClassNotFoundException, IDMapperException {
		DrugBankParser p = new DrugBankParser();
		Set<DrugModel> res = p.parse(new File(args[0]));

		File edgeOut = new File("drugEdges.txt");
		File nodeOut = new File("drugNodes.txt");
		
		PrintWriter writerE = new PrintWriter(new FileOutputStream(edgeOut),true);//new PrintWriter("drugEdges.txt","UTF-8");
		PrintWriter writerN = new PrintWriter(new FileOutputStream(nodeOut),true);
		
		//i chose map here, not sure what is most convenient, hashmap/hashset
		Map<String, HashSet<String>> nodes = new HashMap<String,HashSet<String>>();

		//setting up BridgeDB
		File bridgedb = new File("Hs_Derby_20130701.bridge");
		DataSourceTxt.init();
		Class.forName("org.bridgedb.rdb.IDMapperRdb");
		IDMapper mapper = BridgeDb.connect("idmapper-pgdb:"
				+ bridgedb.getAbsolutePath());	


		for(DrugModel drug : res){
			for(String group: drug.getGroups()){
				
				if(group.equals("approved")){
					
					//mapping drugs by DrugbankID
					String drugId = drug.getDrugbankID(); 
					String drugname = drug.getName();
					LinkedHashSet<String> drugProperties = new LinkedHashSet<String>();
					drugProperties.add(drugname);
					drugProperties.add(drug.getCategories().toString());
					System.out.println(drug.getCategories().toString());
					nodes.put(drugId, drugProperties);
					for(TargetModel target:drug.getTargets()){
						DataSource ds = DataSource.getExistingBySystemCode("S");
						Xref xref= new Xref(target.getUniprotId(),ds);
						//String targetName = target.getName();
						//mapping all the drug targets to Ensembl ID
						Set<Xref> result = mapper.mapID(xref, DataSource.getExistingBySystemCode("En"));
						for(Xref x:result){
							Set<Xref> hgncResult = mapper.mapID(x, DataSource.getExistingBySystemCode("H"));
							LinkedHashSet<String> tName = new LinkedHashSet<String>();
							if(hgncResult.size() > 0) {
								tName.add(hgncResult.iterator().next().getId());
							}
							 
							nodes.putIfAbsent(x.getId(),tName);
							//System.out.println(x.toString()+"\t"+targetName+"\t"+target.getName());
							writerE.println(drugId+"\t"+ x.getId());
						}
					}
				}
			}	
		}
		
		for(String key:nodes.keySet()){
			writerN.println(key+"\t"+nodes.get(key));
		}
		
		writerE.close();
		writerN.close();
	}
}

