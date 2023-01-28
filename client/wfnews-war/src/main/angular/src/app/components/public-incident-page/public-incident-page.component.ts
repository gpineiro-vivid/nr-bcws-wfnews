import { HttpClient } from "@angular/common/http"
import { ChangeDetectorRef, Component, OnInit } from "@angular/core"
import { ActivatedRoute, ParamMap } from "@angular/router"
import { AreaRestrictionsOption, EvacOrderOption } from "../../conversion/models"
import { AGOLService } from "../../services/AGOL-service"
import { PublishedIncidentService } from "../../services/published-incident-service"

@Component({
  selector: 'public-incident-page',
  templateUrl: './public-incident-page.component.html',
  styleUrls: ['./public-incident-page.component.scss']
})
export class PublicIncidentPage implements OnInit {
  public isLoading = true
  public loadingFailed = false

  public incidentNumber: string
  public incident: any
  public evacOrders: EvacOrderOption[] = []
  public areaRestrictions : AreaRestrictionsOption[] = []
  public extent: any = null

  constructor(private router: ActivatedRoute,
              protected cdr: ChangeDetectorRef,
              private agolService: AGOLService,
              private publishedIncidentService: PublishedIncidentService,
              protected http: HttpClient) {

  }

  ngOnInit() {
    this.router.queryParams.subscribe((params: ParamMap) => {
      if (params && params['incidentNumber']) {
        this.incidentNumber = params['incidentNumber']
        // Load the incident from the API
        this.publishedIncidentService.fetchPublishedIncident(this.incidentNumber).toPromise().then(async result => {
          this.incident = result
          // set geometry
          this.incident.geometry = {
            x: result.longitude,
            y: result.latitude
          }
          // format dates, booleans
          // date formatting options
          const options: Intl.DateTimeFormatOptions = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }
          // set date strings
          this.incident.discoveryDate = new Date(this.incident.discoveryDate).toLocaleTimeString("en-US", options)
          this.incident.declaredOutDate = this.incident.declaredOutDate ? new Date(this.incident.declaredOutDate).toLocaleTimeString("en-US", options) : new Date(this.incident.discoveryDate).toLocaleTimeString("en-US", options)
          this.incident.lastUpdatedTimestamp = new Date(this.incident.lastUpdatedTimestamp).toLocaleTimeString("en-US", options)
          // check the contact info
          if (!this.incident.contactOrgUnitIdentifer) {
            this.http.get('../../../../assets/data/fire-center-contacts-agol.json').subscribe(data => {
              this.incident.contactPhoneNumber = data[this.incident.fireCentreCode].phone
              this.incident.contactEmailAddress = data[this.incident.fireCentreCode].url
              this.cdr.detectChanges();
            });
          }
          // fetch the fire perimetre
          await this.getFirePerimetre()
          // load evac orders and area restrictions nearby
          await this.getEvacOrders()
          await this.getExternalUriEvacOrders()
          await this.getAreaRestrictions()
          // activate page
          this.isLoading = false
          this.cdr.detectChanges()
        }).catch(err => {
          console.error(err)
          this.isLoading = false
          this.loadingFailed = true
        })
      } else {
        if(params && params['preview']) {
          this.loadPreview()
        } else {
          this.isLoading = false
          this.loadingFailed = true
        }
      }
    })
  }

  async loadPreview () {
    this.incident = JSON.parse(localStorage.getItem("preview_incident"))
    // fetch the fire perimetre
    await this.getFirePerimetre()
    // load evac orders and area restrictions nearby
    await this.getEvacOrders()
    await this.getExternalUriEvacOrders()
    await this.getAreaRestrictions()
    // activate page
    this.isLoading = false
    this.cdr.detectChanges()
  }

  async getFirePerimetre () {
    return this.agolService.getFirePerimetre(this.incidentNumber, { returnCentroid: true, returnGeometry: true, returnExtent: true}).toPromise().then(response => {
      if (response.extent) {
        this.extent = response.extent
      }
    })
  }

  async getEvacOrders () {
    return this.agolService.getEvacOrdersByEventNumber(this.incidentNumber, { returnCentroid: true, returnGeometry: false}).toPromise().then(response => {
      if (response.features) {
        for (const element of response.features) {
          this.evacOrders.push({
            eventName: element.attributes.EVENT_NAME,
            eventType: element.attributes.EVENT_TYPE,
            orderAlertStatus: element.attributes.ORDER_ALERT_STATUS,
            issuingAgency: element.attributes.ISSUING_AGENCY,
            preOcCode: element.attributes.PREOC_CODE,
            emrgOAAsysID: element.attributes.EMRG_OAA_SYSID,
            uri: null,
            centroid: element.centroid
          })
        }
      }
    })
  }

  async getExternalUriEvacOrders () {
    return this.publishedIncidentService.fetchExternalUri(this.incident.incidentNumberLabel).toPromise().then(results => {
      if (results && results.collection && results.collection.length > 0) {
        for (const uri of results.collection) {
          if (uri.externalUriCategoryTag.includes('EVAC-ORDER')) {
            this.evacOrders.push({
              eventName: uri.externalUriDisplayLabel,
              eventType: uri.externalUriCategoryTag.split(':')[1],
              orderAlertStatus: uri.externalUriCategoryTag.split(':')[1],
              issuingAgency: 'Pending',
              preOcCode: 'NA',
              emrgOAAsysID: 0,
              uri: uri.externalUri,
              centroid: [0, 0]
            })
          }
        }
      }
    }).catch(err => {
      console.error(err)
    })
  }

  async getAreaRestrictions () {
    return this.agolService.getAreaRestrictions({ x: this.incident.longitude, y: this.incident.latitude}).toPromise().then(response => {
      if (response.features) {
        for (const element of response.features) {
          this.areaRestrictions.push({
            protRsSysID: element.attributes.PROT_RA_SYSID,
            name: element.attributes.NAME,
            accessStatusEffectiveDate: element.attributes.ACCESS_STATUS_EFFECTIVE_DATE,
            fireCentre: element.attributes.FIRE_CENTRE_NAME,
            fireZone: element.attributes.FIRE_ZONE_NAME,
            bulletinUrl: element.attributes.BULLETIN_URL
          })
        }
      }
    })
  }
}
