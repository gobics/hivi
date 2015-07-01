package de.gobics.hivi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * The awesome new Database
 *
 * @author Manuel Landesfeind &lt;manuel@gobics.de&gt;
 */
public class Database {

	private final File filename;
	private final ZipFile dbfile;
	private String info = null;
	private SchemaLoader schema = null;

	/**
	 * Creates a new database object from a HiVi-database.
	 *
	 * @param filename
	 * @throws ZipException if ZIP error occurred (e.g. not a ZIP file)
	 * @throws IOException if an IO-Error occurred
	 */
	public Database(File filename) throws ZipException, IOException {
		this.filename = filename;
		this.dbfile = new ZipFile(filename);
		if (dbfile.getEntry("schema") == null || dbfile.getEntry("mapping") == null) {
			throw new IllegalArgumentException("Given file is not a valid HiVi database: " + filename.
					getAbsolutePath());
		}
	}
	
	public File getFile(){
		return filename;
	}

	public String getInfo() throws IOException {
		if (info == null && dbfile.getEntry("info") != null) {
			ZipEntry entry = dbfile.getEntry("info");
			BufferedReader in = new BufferedReader(new InputStreamReader(dbfile.
					getInputStream(entry)));
			String line = null;
			while ((line = in.readLine()) != null) {
				info += line;
			}
			in.close();
		}
		return info;
	}

	public SchemaLoader getSchemaLoader() throws IOException {
		if (schema == null) {
			ZipEntry entry = dbfile.getEntry("schema");
			schema = new SchemaLoader(filename.getName(), dbfile.getInputStream(entry));
		}
		return schema;
	}

	public MappingReadIterator getMappingIterator() throws IOException {
		ZipEntry entry = dbfile.getEntry("mapping");
		BufferedReader in = new BufferedReader(new InputStreamReader(dbfile.getInputStream(entry)));
		return new MappingReadIterator(in);
	}
	
	public int countMappings() throws IOException{
		ZipEntry entry = dbfile.getEntry("mapping");
		BufferedReader in = new BufferedReader(new InputStreamReader(dbfile.getInputStream(entry)));
		int lines = 0;
		while(in.readLine() != null)lines++;
		return lines;
	}
}
