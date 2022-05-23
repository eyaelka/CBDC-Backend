//package com.template.schema.endUserSchemas;
//
//import com.template.model.endUser.EndUserData;
//import com.template.schema.BankAccountPersistant;
//import com.template.schema.CBDCFamilySchema;
//import com.template.schema.UsersAccountPersistant;
//import lombok.Getter;
//import net.corda.core.schemas.MappedSchema;
//import net.corda.core.schemas.PersistentState;
//import org.hibernate.annotations.Type;
//
//import javax.annotation.Nullable;
//import javax.persistence.*;
//import java.util.*;
//
//public class EndUserStateSchemaV1 extends MappedSchema {
//
//    public EndUserStateSchemaV1() {
//        super(CBDCFamilySchema.class, 1, Arrays.asList(PersistentEndUserStateSchema.class,
//                UsersAccountPersistant.class));
//    }
//
//    @Nullable
//    @Override
//    public String getMigrationResource() {
//        return "iou.changelog-master";
//    }
//
//    @Entity
//    @Table(name = "end_user")
//    @Getter
//    public static class PersistentEndUserStateSchema extends PersistentState {
//
//        @Column(name = "cin")
//        private final String cin;
//        @Column(name = "nom")
//        private final String nom;
//        @Column(name = "date_naissance")
//        private final String dateNaissance;
//        @Column(name = "email")
//        private final String email;
//        @Column(name = "nationalite")
//        private final String nationalite;
//        @Column(name = "adresse")
//        private final String adresse;
//        @Column(name = "telephone")
//        private final String telephone;
//        @Column(name = "bank_who_add_user")
//        private final String bankWhoAddUser;
//
//        @Column(name = "added_by")
//        private final String addedBy;
//        @Column(name = "owner")
//        private final String owner;
//        @Column(name = "linearid")
//        @Type(type = "uuid-char")
//        private final UUID linearId;
//
//
//        @OneToMany(targetEntity = UsersAccountPersistant.class, mappedBy = "persistentEndUserStateSchema",cascade = CascadeType.PERSIST)
//        private final List<UsersAccountPersistant> endUserAccounts;
//
//        public PersistentEndUserStateSchema(EndUserData endUserData, List<UsersAccountPersistant> usersAccountPersistants,
//                                            String addedBy, String owner, UUID linearId) {
//            this.addedBy = addedBy;
//            this.owner = owner;
//            this.linearId = linearId;
//            cin = endUserData.getCin();
//            nom = endUserData.getNom();
//            dateNaissance = endUserData.getDateNaissance().toString();
//            adresse = endUserData.getAdresse();
//            email = endUserData.getEmail();
//            telephone = endUserData.getTelephone();
//            bankWhoAddUser = endUserData.getBankWhoAddUser();
//            nationalite = endUserData.getNationalite();
//            endUserAccounts = usersAccountPersistants;
//        }
//
//        // Default constructor required by hibernate.
//        public PersistentEndUserStateSchema() {
//            this.addedBy = null;
//            this.linearId = null;
//            this.owner = null;
//            nom = null;
//            nationalite = null;
//            adresse = null;
//            telephone = null;
//            email = null;
//            cin = null;
//            bankWhoAddUser = null;
//            dateNaissance = null;
//            endUserAccounts = null;
//        }
//    }
//}
//
