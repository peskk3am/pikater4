package pikater.agents;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pikater.configuration.Argument;
import pikater.logging.Logger;
import pikater.logging.Verbosity;
import pikater.ontology.messages.MessagesOntology;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Kuba
 * Date: 25.8.13
 * Time: 9:38
 */
public abstract class PikaterAgent extends Agent {
    protected Codec codec = new SLCodec();
    protected Ontology ontology = MessagesOntology.getInstance();
    protected String initBeansName = "Beans.xml";
    protected ApplicationContext context =  new ClassPathXmlApplicationContext(initBeansName);
    protected Verbosity verbosity=Verbosity.NORMAL;
    private Logger logger=(Logger) context.getBean("logger");
    protected Map<String,Argument> arguments;

    public Codec getCodec() {
        return codec;
    }

    public Ontology getOntology() {
        return ontology;
    }

    public String GetArgumentValue(String argName)
    {
        return arguments.get(argName).getValue();
    }

    public Boolean ContainsArgument(String argName)
    {
        return arguments.containsKey(argName);
    }

    public void ParseArguments(Object[] args)
    {
        if (args==null)
        {
            return;
        }
        arguments=new HashMap<>();
        for (Object arg:args)
        {
               if (arg instanceof Argument)
               {
                      Argument argumentToAdd=(Argument)arg;
                      arguments.put(argumentToAdd.getName(),argumentToAdd);
               }
            else {
                   throw new IllegalArgumentException();
               }
        }
    }

    protected void print(String text, Verbosity level){
       print(text,level.ordinal());
    }

    protected void print(String text, int level){
        if (verbosity.ordinal() >= level){
             logger.print(getLocalName() + ": "+text);
        }
    }

    protected void println(String text,Verbosity level)
    {
        println(text,level.ordinal());
    }

    protected void println(String text, int level){
        if (verbosity.ordinal() >= level){
            logger.println(getLocalName() + ": "+text);
        }
    }
}
