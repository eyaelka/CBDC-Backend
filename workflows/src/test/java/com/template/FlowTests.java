package com.template;

import net.corda.testing.node.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FlowTests {
    private MockNetwork network;
    private StartedMockNode a;
    private StartedMockNode b;

    @Before
    public void setup() {
       /* network = new MockNetwork(new MockNetworkParameters().withCordappsForAllNodes(ImmutableList.of(
                TestCordapp.findCordapp("com.template.contracts"),
                TestCordapp.findCordapp("com.template.flows")))
                .withNotarySpecs(ImmutableList.of(new MockNetworkNotarySpec(CordaX500Name.parse("O=Notary,L=London,C=GB")))));
        a = network.createPartyNode(null);
        b = network.createPartyNode(null);
        network.runNetwork();

        */
    }

    @After
    public void tearDown() {
       // network.stopNodes();
    }

    @Test
    public void dummyTest() {
        /*TemplateFlow.TemplateFlowInitiator flow = new TemplateFlow.TemplateFlowInitiator(b.getInfo().getLegalIdentities().get(0));
        Future<SignedTransaction> future = a.startFlow(flow);
        network.runNetwork();

        //successful query means the state is stored at node b's vault. Flow went through.
        QueryCriteria inputCriteria = new QueryCriteria.VaultQueryCriteria().withStatus(Vault.StateStatus.UNCONSUMED);
        CentralBankStoreState state = b.getServices().getVaultService().queryBy(CentralBankStoreState.class,inputCriteria)
                .getStates().get(0).getState().getData();
    */}
}
