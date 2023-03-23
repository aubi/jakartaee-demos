class MandelbrotSelector extends HTMLElement {
    xmin = 0;
    xmax = 0;
    ymin = 0;
    ymax = 0;
    dimension = 1000;
    iterations = 500;
    bailout = 4;
    generatinginfo = null;

    constructor() {
        super();
        this.shadow = this.attachShadow({
            mode: 'open',
            delegateFocus: true
        });
    }
    
    static get observedAttributes() {
        return ['xmin', 'xmax', 'ymin', 'ymax', 'dimension', 'iterations', 'bailout', 'generatinginfo'];
    }
    
    attributeChangedCallback(name, oldValue, newValue) {
        console.log("attributeChangedCallback", name, oldValue, newValue);
        switch(name) {
            case 'xmin':
                this.xmin = Number.parseFloat(newValue) || 0;
                break;
            case 'xmax':
                this.xmax = Number.parseFloat(newValue) || 0;
                break;
            case 'ymin':
                this.ymin = Number.parseFloat(newValue) || 0;
                break;
            case 'ymax':
                this.ymax = Number.parseFloat(newValue) || 0;
                break;
            case 'iterations':
                this.iterations = Number.parseInt(newValue) || 0;
                break;
            case 'dimension':
                this.dimension = Number.parseInt(newValue) || 0;
                break;
            case 'bailout':
                this.bailout = Number.parseInt(newValue) || 0;
                break;
        }
    }
    
    connectedCallback() {
        this.recreate();
    }

    recreate() {
        console.log("MandelbrotPicture.render",this.xmin,this.xmax);
        var html = `
            <p>x: ${this.xmin} &ndash; ${this.xmax}, y: ${this.ymin} &ndash; ${this.ymax}</p>
        `;
        if(this.generatinginfo === null) {
            html += `
                <p><button onclick='this.getRootNode().host.generateVideo(event,this.getRootNode().host)'>Generate Video!</button></p>
                <img width=${this.dimension} height=${this.dimension} onclick='this.getRootNode().host.handleClick(event)' src='/MandelbrotPicture/rest/mandelbrot?xMin=${this.xmin}&xMax=${this.xmax}&yMin=${this.ymin}&yMax=${this.ymax}&iterations=${this.iterations}&dimension=${this.dimension}&bailout=${this.bailout}' />
                <p>Click to image to zoom in, Shifl+click to zoom out.</p>
            `;
        } else {
            if(this.generatinginfo.finalFinished === false) {
                // still generating
                html += `
                    <p>Generating video #${this.generatinginfo.id}, rendering images: ${this.generatinginfo.status}, processing video: ${this.generatinginfo.renderingInfo}</p>
                    <p>Images:  
                `;
                var processed = this.generatinginfo.renderedImageProgress;
                // &#8203; is zero-width breakable space
                this.generatinginfo.images.forEach(img => html += "&#8203;" + (img==="invideo" ? "🎥️" : img==="rendered" ? "🖼" : "🖌️️"));
                html += "</p>";
            } else {
                // generation finished
                html += `
                    <p>Video is ready!</p>
                    <p>
                        <video width="${this.dimension}" height="${this.dimension}" controls>
                        <source src="rest/mandelbrot/cache/${this.generatinginfo.id}/video" type="video/mp4">
                      Your browser does not support the video tag.
                      </video> 
                    </p>
                `;
            }
        }
        this.shadow.innerHTML = html;
    }
    
    handleClick(event) {
        var x = event.pageX - event.target.offsetLeft;
        var y = event.pageY - event.target.offsetTop;
        console.log("MandelbrotPicture.handleClick @ ["+x+", "+y+"]", event);
        var pixelDx = event.target.width;
        var pixelDy = event.target.height;
        var realDx = (this.xmax-this.xmin);
        var realDy = (this.ymax-this.ymin);
        var centerx = this.xmin + x * realDx / pixelDx;
        var centery = this.ymin + y * realDy / pixelDy;
        var newDx = realDx/4;
        var newDy = realDy/4;
        if(event.shiftKey) { // zoom out
            newDx = realDx*2;
            newDy = realDy*2;
        }
        this.xmin = centerx - newDx;
        this.xmax = centerx + newDx;
        this.ymin = centery - newDy;
        this.ymax = centery + newDy;
        this.recreate();
    }
    
    generateVideo(event, caller) {
        fetch(`rest/mandelbrot/asynch?xMin=${this.xmin}&xMax=${this.xmax}&yMin=${this.ymin}&yMax=${this.ymax}&iterations=${this.iterations}&dimension=${this.dimension}&bailout=${this.bailout}`).then(function (response) {
            // The API call was successful!
            return response.json();
        }).then(function (data) {
            // This is the JSON from our response
            console.log(data);
            caller.updateGeneratingInfo(data, caller);
        }).catch(function (err) {
            // There was an error
            console.log('Generation of video failed', err);
            alert('Generation of video failed: '+ err);
        });
    }
    
    fetchProcessingInfo(id, caller) {
        fetch(`rest/mandelbrot/cache/${id}/state`).then(function (response) {
            // The API call was successful!
            return response.json();
        }).then(function (data) {
            // This is the JSON from our response
            console.log(data);
            caller.updateGeneratingInfo(data, caller);
        }).catch(function (err) {
            // There was an error
            console.log('Generation of video failed', err);
            alert('Generation of video failed: '+ err);
        });
    }
    
    updateGeneratingInfo(data, caller) {
        this.generatinginfo = data;
        this.recreate();
        if(data.finalFinished === false) {
            setTimeout(function() {caller.fetchProcessingInfo(data.id, caller)}, 1000);
        }
    }
}

customElements.define("mandelbrot-selector", MandelbrotSelector);
