package pikater.configuration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 18.8.13
 * Time: 9:47
 * Provides configuration from XML file
 */
public class XmlConfigurationProvider implements ConfigurationProvider {
    private String filePath;

    public XmlConfigurationProvider(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Configuration getConfiguration() {
        List<AgentConfiguration> agentConfigurations=new ArrayList<>();
        try {

            File fXmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("agent");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                AgentConfiguration agent= getAgent(nNode);
                agentConfigurations.add(agent);
                }
            }
        }
         catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new ConfigurationImpl(agentConfigurations);
    }

    private AgentConfiguration getAgent(Node nNode) {
                Element eElement = (Element) nNode;
                String name= eElement.getAttribute("name");
                String type= eElement.getAttribute("type");
                List<Argument> arguments=getArguments(eElement);
                return new AgentConfigurationImpl(name,type,arguments);
    }

    private List<Argument> getArguments(Element agentElement)
    {
        List<Argument> arguments=new ArrayList<>();
        NodeList argumentsNodeList= agentElement.getElementsByTagName("argument");
        for (int argNr=0;argNr<argumentsNodeList.getLength();argNr++)
        {
            Node argNode = argumentsNodeList.item(argNr);
            if (argNode.getNodeType() == Node.ELEMENT_NODE) {

                Element arg = (Element) argNode;
                String key= arg.getAttribute("key");
                String value=arg.getAttribute("value");
                Argument argument=new Argument(key,value);
                if (arg.hasAttribute("sendOnlyValue"))
                {
                      argument.setSendOnlyValue(true);
                }
                arguments.add(argument);
            }
        }
        return arguments;
    }
}

