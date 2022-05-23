//package com.template.schema.commercialBankSchemas;
//
//import com.template.model.commercialBank.CommercialBankData;
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
//public class CommercialBankStateSchemaV1 extends MappedSchema {
//
//    public CommercialBankStateSchemaV1() {
//        super(CBDCFamilySchema.class, 1, Arrays.asList(PersistentCommercialBankStateSchema.class,
//                BankAccountPersistant.class));
//    }
//
//    @Nullable
//    @Override
//    public String getMigrationResource() {
//        return "iou.changelog-master";
//    }
//
//    @Entity
//    @Table(name = "commercial_bank")
//    @Getter
//    public static class PersistentCommercialBankStateSchema extends PersistentState {
//
//        @Column(name = "name")
//        private final String name;
//        @Column(name = "abreviation")
//        private final String abreviation;
//        @Column(name = "email")
//        private final String email;
//        @Column(name = "fax")
//        private final String fax;
//        @Column(name = "address")
//        private final String address;
//        @Column(name = "pays")
//        private final String pays;
//        @Column(name = "added_by")
//        private final String addedBy;
//        @Column(name = "owner")
//        private final String owner;
//        @Column(name = "linearid")
//        @Type(type = "uuid-char")
//        private final UUID linearId;
//
//        @OneToMany(targetEntity = BankAccountPersistant.class, mappedBy = "persistentCommercialBankStateSchema",cascade = CascadeType.PERSIST)
//
//        private final List<BankAccountPersistant> commercialBankAccounts;
//
//        public PersistentCommercialBankStateSchema(CommercialBankData commercialBankData, List<BankAccountPersistant> bankAccountPersistants,
//                                                   String addedBy, String owner, UUID linearId) {
//            this.addedBy = addedBy;
//            this.owner = owner;
//            this.linearId = linearId;
//            name = commercialBankData.getName();
//            pays = commercialBankData.getPays();
//            address = commercialBankData.getAddress();
//            email = commercialBankData.getEmail();
//            fax = commercialBankData.getFax();
//            abreviation = commercialBankData.getAbreviation();
//            commercialBankAccounts = bankAccountPersistants;
//        }
//
//        // Default constructor required by hibernate.
//        public PersistentCommercialBankStateSchema() {
//            this.addedBy = null;
//            this.linearId = null;
//            this.owner = null;
//            name = null;
//            pays = null;
//            address = null;
//            abreviation = null;
//            email = null;
//            fax = null;
//            commercialBankAccounts = null;
//        }
//
//    }
//}
