const input = document.querySelector('.searchBox input');
const showDom = document.querySelector('.show');

console.log("start");
input.addEventListener('change', function(){

    if(this.value = null){
        return;
    }



    fetch('/searchContent?keyword=' + this.value)
       .then(function(response) {
        return response.json();
       })
      .then(function(body){
        if(!body){
            return;
        }

        let songsData = body.songs.content;
        function createItem(item) {
          const li = document.createElement('li');
          li.innerHTML = `
                <li>
                    <div class="icon" style="background:url(${item.cover}) no-repeat center;background-size: contain;">

                    </div>
                    <h4>${item.name}</h4>
                </li>`;
              return li;
            }


            showDom.innerHTML = '';
            for (const songItem of songsData) {
              console.log(songItem);
              const li = createItem(songItem);
              showDom.appendChild(li);
            }
      });


})
































