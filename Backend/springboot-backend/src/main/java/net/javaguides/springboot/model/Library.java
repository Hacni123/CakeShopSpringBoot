package net.javaguides.springboot.model;

import javax.persistence.*;  
import java.util.HashSet;  
import java.util.Set;

@Entity(name = "Library")
public class Library {  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL)
    private Set<Book> books = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;

        for(Book b : books) {
            b.setLibrary(this);
        }
    }
}