package ch.tkuhn.nanobrowser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class Sentence extends Thing {
	
	private static final long serialVersionUID = -7967327315454171639L;
	
	public static final String TYPE_URI = "http://krauthammerlab.med.yale.edu/nanopub/claims/claim";
	public static final String CLAIM_URI_BASE = "http://krauthammerlab.med.yale.edu/nanopub/claims/";
	
	public Sentence(String uri) {
		super(uri);
	}
	
	public static Sentence withText(String sentenceText) {
		try {
			return new Sentence(CLAIM_URI_BASE + "en/" + URLEncoder.encode(sentenceText, "UTF8"));
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	private static final String allSentencesQuery =
		"select distinct ?s where {?a ex:asSentence ?s}";
	
	public static List<Sentence> getAllSentences(int limit) {
		String lm = (limit >= 0) ? " limit " + limit : "";
		List<BindingSet> result = TripleStoreAccess.getTuples(allSentencesQuery + lm);
		List<Sentence> l = new ArrayList<Sentence>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("s");
			if (v instanceof BNode) continue;
			l.add(new Sentence(v.stringValue()));
		}
		return l;
	}
	
	public static boolean isSentenceURI(String uri) {
		return uri.startsWith(CLAIM_URI_BASE);
	}
	
	public static boolean isSentenceText(String text) {
		if (text.indexOf("/") > -1) return false;
		if (text.indexOf(" ") == -1) return false;
		if (text.length() < 10 || text.length() > 500) return false;
		return text.substring(text.length()-1).equals(".");
	}
	
	private static final String nanopubsQuery =
		"select distinct ?p where { ?p np:hasAssertion ?a . ?a ex:asSentence <@> . ?p np:hasProvenance ?prov . " +
		"?prov np:hasAttribution ?att . graph ?att { ?x dc:created ?d } }";
	
	public List<Nanopub> getNanopubs() {
		String query = nanopubsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Nanopub> nanopubs = new ArrayList<Nanopub>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			nanopubs.add(new Nanopub(v.stringValue()));
		}
		return nanopubs;
	}

	private static final String opinionsQuery =
		"select ?p ?t ?pub where { " +
		"?pub np:hasAssertion ?ass . ?pub np:hasProvenance ?prov . " +
		"?prov np:hasAttribution ?att . graph ?att { ?x dc:created ?d } . " +
		"graph ?ass { ?p ex:hasOpinion ?o . ?o ex:opinionType ?t . ?o ex:opinionOn ?s } ." +
		"{ ?ass2 ex:asSentence <@> . ?ass2 ex:asSentence ?s } union " +
		"{ <@> ex:hasSameMeaning ?s } union { ?s ex:hasSameMeaning <@> } " +
		"} order by asc(?d)";
	
	public List<Opinion> getOpinions(boolean excludeNullOpinions) {
		String query = opinionsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		Map<String, Opinion> opinionMap = new HashMap<String, Opinion>();
		for (BindingSet bs : result) {
			Value p = bs.getValue("p");
			Value t = bs.getValue("t");
			Value pub = bs.getValue("pub");
			if (p instanceof BNode || t instanceof BNode || pub instanceof BNode) continue;
			if (excludeNullOpinions && t.stringValue().equals(Opinion.NULL_TYPE)) {
				opinionMap.remove(p.stringValue());
			} else {
				Person person = new Person(p.stringValue());
				Nanopub nanopub = new Nanopub(pub.stringValue());
				Opinion opinion = new Opinion(person, t.stringValue(), this, nanopub);
				opinionMap.put(p.stringValue(), opinion);
			}
		}
		return new ArrayList<Opinion>(opinionMap.values());
	}
	
	private static final String sameMeaningSentencesQuery =
		"select ?s ?pub where { { " +
		"{ ?pub np:hasAssertion ?ass . ?ass ex:asSentence <@> . ?ass ex:asSentence ?s } union " +
		"{ ?pub np:hasAssertion ?ass . graph ?ass { <@> ex:hasSameMeaning ?s } } union " +
		"{ ?pub np:hasAssertion ?ass . graph ?ass { ?s ex:hasSameMeaning <@> } } " +
		"} . ?pub np:hasProvenance ?prov . ?prov np:hasAttribution ?att . graph ?att { ?x dc:created ?d } . " +
		"} order by asc(?d)";
	
	public List<Pair<Sentence,Nanopub>> getSameMeaningSentences() {
		String query = sameMeaningSentencesQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		Map<String, Pair<Sentence,Nanopub>> sentencesMap = new HashMap<String, Pair<Sentence,Nanopub>>();
		for (BindingSet bs : result) {
			Value s = bs.getValue("s");
			Value pub = bs.getValue("pub");
			if (s instanceof BNode || pub instanceof BNode) continue;
			if (!s.stringValue().equals(getURI())) {
				Sentence sentence = new Sentence(s.stringValue());
				Nanopub nanopub = new Nanopub(pub.stringValue());
				sentencesMap.put(sentence.getURI(), new ImmutablePair<Sentence,Nanopub>(sentence, nanopub));
			}
		}
		return new ArrayList<Pair<Sentence,Nanopub>>(sentencesMap.values());
	}
	
	private static final String publishSameMeaning =
		"prefix : <@I> insert data into graph : { :Pub a ex:MetaNanopub . :Pub np:hasAssertion :Ass . " +
		":Pub np:hasProvenance :Prov . :Prov np:hasAttribution :Att . :Prov np:hasSupporting :Supp } \n\n" +
		"prefix : <@I> insert data into graph :Ass { <@T> ex:hasSameMeaning <@O> } \n\n" +
		"prefix : <@I> insert data into graph :Att { :Pub pav:authoredBy <@P> . :Pub pav:createdBy <@P> . " +
		":Pub dc:created \"@D\"^^xsd:dateTime }";
	
	public void publishSameMeaning(Sentence other, Person author) {
		String pubURI = "http://foo.org/" + (new Random()).nextInt(1000000000);
		String query = publishSameMeaning
				.replaceAll("@I", pubURI)
				.replaceAll("@P", author.getURI())
				.replaceAll("@T", getURI())
				.replaceAll("@O", other.getURI())
				.replaceAll("@D", NanobrowserApplication.getTimestamp());
		TripleStoreAccess.runUpdateQuery(query);
	}
	
	private static final String getAllOpinionGraphsQuery =
		"select ?g ?ass ?att where { graph ?ass { ?a ex:hasSameMeaning ?c } . " +
		"graph ?g { ?pub np:hasAssertion ?ass . ?pub np:hasProvenance ?prov . ?prov np:hasAttribution ?att } }";
	private static final String deleteGraphQuery =
		"delete from graph identified by <@> { ?a ?b ?c } where  { ?a ?b ?c }";
	
	public static void deleteAllOpinionNanopubs() {
		for (BindingSet bs : TripleStoreAccess.getTuples(getAllOpinionGraphsQuery)) {
			TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", bs.getValue("g").stringValue()));
			TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", bs.getValue("ass").stringValue()));
			TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", bs.getValue("att").stringValue()));
		}
	}
	
	public String getSentenceText() {
		try {
			return URLDecoder.decode(getLastPartOfURI(), "UTF8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public SentenceItem createGUIItem(String id) {
		return new SentenceItem(id, this);
	}

}
