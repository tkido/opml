package main

import (
	"bytes"
	"encoding/xml"
	"fmt"
	"io/ioutil"
	"log"
	"os"
)

type OPML struct {
	XMLName xml.Name `xml:"opml"`
	Head    string   `xml:"head"`
	Body    Body     `xml:"body"`
}

type Body struct {
	Outlines []Outline `xml:"outline"`
}

type Outline struct {
	Title string `xml:"title,attr"`
	Items []Item `xml:"outline"`
}

type Item struct {
	Title string `xml:"title,attr"`
	URL   string `xml:"htmlUrl,attr"`
}

const fomaTmpl = `<div class="sidebody"><a href="%s" target="_blank">%s</a></div>`
const godTmpl = `<li><a href="%s" target="_blank">%s</a></li>`

func main() {
	xmlFile, err := os.Open("data/export.xml")
	if err != nil {
		log.Fatal(err)
		return
	}
	defer xmlFile.Close()
	xmlData, err := ioutil.ReadAll(xmlFile)
	if err != nil {
		log.Fatal(err)
		return
	}

	var opml OPML
	xml.Unmarshal(xmlData, &opml)

	foma := bytes.Buffer{}
	god := bytes.Buffer{}

	for _, o := range opml.Body.Outlines {
		switch o.Title {
		case "まとめ":
			// pass
		case "それにつけても金のほしさよ":
			for _, i := range o.Items {
				s := fmt.Sprintf(fomaTmpl, i.URL, i.Title)
				foma.WriteString(s)
				foma.WriteString("\n")
			}
		default:
			head := fmt.Sprintf("<strong>%s</strong>\n<ul>\n", o.Title)
			god.WriteString(head)
			for _, i := range o.Items {
				s := fmt.Sprintf(godTmpl, i.URL, i.Title)
				god.WriteString(s)
				god.WriteString("\n")
			}
			god.WriteString("</ul>\n")
		}
	}

	fmt.Println(foma.String())
	fmt.Println(god.String())
}
