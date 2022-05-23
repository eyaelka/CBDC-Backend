//package com.template.schema.centralBankSchemas;
//
//import com.template.model.centralBank.CentralBank;
//import com.template.model.centralBank.CentralBankData;
//import com.template.schema.BankAccountPersistant;
//import com.template.schema.CBDCFamilySchema;
//import lombok.Getter;
//import net.corda.core.schemas.MappedSchema;
//import net.corda.core.schemas.PersistentState;
//import org.hibernate.annotations.Type;
//
//import javax.annotation.Nullable;
//import javax.persistence.*;
//import java.util.*;
//
//public class CentralBankStateSchemaV1 extends MappedSchema {
//
//    public CentralBankStateSchemaV1() {
//        super(CBDCFamilySchema.class, 1, Arrays.asList(PersistentCentralBankStateSchema.class,
//             BankAccountPersistant.class));
//
//    }
//
//    @Nullable
//    @Override
//    public String getMigrationResource() {
//        return "iou.changelog-master";
//    }
//
//    @Entity
//    @Table(name = "central_bank")
//    @Getter
//    public static class PersistentCentralBankStateSchema extends PersistentState {
//
//        @Column(name = "nom")
//        private final String nom;
//        @Column(name = "pays")
//        private final String pays;
//        @Column(name = "adresse")
//        private final String adresse;
//        @Column(name = "loicreation")
//        private final String loiCreation;
//        @Column(name = "email")
//        private final String email;
//        @Column(name = "sender")
//        private final String sender;
//        @Column(name = "linearid")
//        @Type(type = "uuid-char")
//        private final UUID linearId;
//
//        @OneToMany(targetEntity = BankAccountPersistant.class, mappedBy = "persistentCentralBankStateSchema", cascade = CascadeType.PERSIST)
//        private final List<BankAccountPersistant> centralBankAccounts;
//
//        public PersistentCentralBankStateSchema(CentralBankData centralBankData, List<BankAccountPersistant> centralBankAccounts, String sender, UUID linearId) {
//            this.sender = sender;
//            this.linearId = linearId;
//            nom = centralBankData.getNom();
//            pays = centralBankData.getPays();
//            adresse = centralBankData.getAdresse();
//            loiCreation = centralBankData.getLoiCreation();
//            email = centralBankData.getEmail();
//            this.centralBankAccounts = centralBankAccounts;
//            System.out.println("Account from stored DB "+centralBankAccounts);
//        }
//
//        // Default constructor required by hibernate.
//        public PersistentCentralBankStateSchema() {
//            this.sender = null;
//            this.linearId = null;
//            nom = null;
//            pays = null;
//            adresse = null;
//            loiCreation = null;
//            email = null;
//            centralBankAccounts = null;
//        }
//
//    }
//
//}
