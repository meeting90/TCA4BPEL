package cn.edu.nju.cs.tcao4bpel.o;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.xml.namespace.QName;

import org.apache.ode.bpel.o.Serializer;

/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * AspectSerializer.java
 */
public class AspectSerializer extends  Serializer{
	
	public AspectSerializer(){
		super();
	}
	public AspectSerializer(long compileTime) {
		super(compileTime);
	}
	
	@Override
	public void read(InputStream is) throws IOException {
		DataInputStream oin = new DataInputStream(is);
		byte[] magic = new byte[MAGIC_NUMBER.length];
		oin.read(magic, 0 ,magic.length);
		if(Arrays.equals(MAGIC_NUMBER, magic)){
			this.format = oin.readShort();
			this.compileTime= oin.readLong();
			String tns= oin.readUTF();
			String name = oin.readUTF();
			this.type= new QName(tns, name);
			return;
		}
		throw new IOException("Unrecongnized file format (bad magic number).");
		
	}
	public void writeOApsect(OAspect oaspect, OutputStream os) throws IOException{
		DataOutputStream out = new DataOutputStream(os);
		out.write(MAGIC_NUMBER);
		out.writeShort(format);
		out.writeLong(compileTime);
		out.writeUTF(oaspect.getTargetNamespace());
		out.writeUTF(oaspect.getAspectName());
		out.flush();
		ObjectOutputStream oos = new CustomObjectOutputStream(os);
		oos.writeObject(oaspect);
		oos.flush();
		oos.close();
		os.close();
		
		
	}
	public OAspect readOAspect(InputStream is) throws IOException{
		OAspect oaspect =null;
		
		try {
			read(is);
			oaspect = (OAspect) new CustomObjectInputStream(is).readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IOException("DataStream error");
		}
		return oaspect;
		
		
	}

}
