import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';
import { FormsModule, FormGroup, FormControl } from '@angular/forms';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { FollowupAction } from '../entities/followup-action/followup-action.model';
import { ResponseItem } from '../entities/followup-action/response-item.model';
import { FollowupActionService } from '../entities/followup-action/followup-action.service';
import { ResponseWrapper } from '../shared';
import { FormsService } from './forms.service';

@Component({
    selector: 'moxfq-component',
    templateUrl: './moxfq.component.html'
})
export class MoxfqComponent implements OnInit {

    @Input() followupAction: FollowupAction;
    isSaving: boolean;
    formData: any;

    constructor(
        private jhiAlertService: JhiAlertService,
        private followupActionService: FollowupActionService,
        private eventManager: JhiEventManager,
        private formsService: FormsService
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.formData = {};
        if (this.followupAction.responseItems) {
            this.formsService.convertToFormData(this.followupAction.responseItems, this.formData);
        }
    }

    save() {
        this.isSaving = true;
        this.subscribeToSaveResponse(
            this.followupActionService.update(this.followupAction));
    }

    private subscribeToSaveResponse(result: Observable<FollowupAction>) {
        result.subscribe((res: FollowupAction) =>
            this.onSaveSuccess(res), (res: Response) => this.onError(res));
    }

    private onSaveSuccess(result: FollowupAction) {
        this.eventManager.broadcast({ name: 'followupActionListModification', content: result});
        this.isSaving = false;
    }

    submitData(data: any) {
        console.log("form data  = " , data );
        // loop through data keys and collect as response items
        let items: Array<ResponseItem> = [];
        Object.keys(data).forEach((key) => {
            if(key != 'comment' && data[key]) {
                items.push(this.formsService.convertToResponseItem(key, data[key], this.followupAction));
            }
        });
        console.log("items  = " , items );
        this.followupAction.responseItems = items;
        this.save();
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }
}
