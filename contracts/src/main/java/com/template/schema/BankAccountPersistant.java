//package com.template.schema;
//
//import com.template.schema.centralBankSchemas.CentralBankStateSchemaV1;
//import com.template.schema.commercialBankSchemas.CommercialBankStateSchemaV1;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//import java.util.Date;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
////@CordaSerializable
//@Entity
//@Table(name = "bank_account_persistant")
//public class BankAccountPersistant {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "bank_account_persistant_id")
//    private Long bankAccountPersistantId;
//    @Column(name = "account_id")
//    private String accountId;
//    @Column(name = "password")
//    private String password;
//    @Column(name = "suspend")
//    private boolean suspend = false; // suspend = true the account is suspend, else false. false is default value
//    @Column(name = "account_type")
//    private String accountType ="courant"; // accountType = epargne or courant. courant by default
//    @Column(name = "CRUD_date")
//    private Date CRUDDate;
//
//    @Column(name = "central_bank_output_index")
//    private int central_bank_output_index;
//    @Column(name = "central_bank_transaction_id")
//    private String central_bank_transaction_id;
//    @Column(name = "commercial_bank_output_index")
//    private int commercial_bank_output_index;
//    @Column(name = "commercial_bank_transaction_id")
//    private String commercial_bank_transaction_id;
//
//    @ManyToOne
//    @JoinColumns({
//            @JoinColumn(name = "central_bank_output_index", referencedColumnName = "output_index"),
//            @JoinColumn(name = "central_bank_transaction_id", referencedColumnName = "transaction_id"),
//    })
//    CentralBankStateSchemaV1.PersistentCentralBankStateSchema persistentCentralBankStateSchema ;
//    @ManyToOne
//    @JoinColumns({
//            @JoinColumn(name = "commercial_bank_output_index", referencedColumnName = "output_index"),
//            @JoinColumn(name = "commercial_bank_transaction_id", referencedColumnName = "transaction_id"),
//    })
//    CommercialBankStateSchemaV1.PersistentCommercialBankStateSchema  persistentCommercialBankStateSchema;
//
//
//
//}
